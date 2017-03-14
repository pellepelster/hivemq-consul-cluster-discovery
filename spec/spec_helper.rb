require 'serverspec'
require 'rspec/wait'
require 'docker'
require 'docker/compose'
require 'uri'
require 'diplomat'
require 'compose_wrapper'
require 'mqtt'

CONSUL_MASTER_TOKEN="11111111-1111-1111-1111-111111111111".freeze

module Helpers

  def socket_reachable(host, port)
    TCPSocket.new(host, port)
    true
  rescue
    false
  end

  def init_environment
    compose.up('consul-server', detached: true)
    compose.up('consul-agent1', detached: true)
    compose.up('consul-agent2', detached: true)
    init_consul
    compose.up('hivemq_node1', detached: true)
    compose.up('hivemq_node2', detached: true)

    host1, port1 = compose.address('hivemq_node1', 1883, index: 1)
    wait_for { socket_reachable(host1, port1) }.to be true

    host2, port2 = compose.address('hivemq_node2', 1883, index: 1)
    wait_for { socket_reachable(host2, port2) }.to be true

  end

  def cleanup_environment
    compose.kill
    compose.rm(force: true)
  end

  def compose
    @compose ||= ComposeWrapper.new
  end

  def docker_uri
    @docker_uri ||= URI(Docker.url)
  end

  def container_id(image_name)
    compose.ps.where { |c| !c.nil? && c.image == image_name }.first.id
  end

  def extract_ip(container_id)
    Docker::Container.get(
        container_id
    ).info['NetworkSettings']['IPAddress']
  end

  def mac_os?
    !RUBY_PLATFORM.match('darwin').nil?
  end

  def container_host(image_name)
    fallback_ip = extract_ip(container_id(image_name))
    uri = URI(Docker.url)
    return '127.0.0.1' if mac_os? && uri.host.to_s.empty?
    uri.host.to_s.empty? ? fallback_ip : uri.host
  end

  def consul_address
    "http://#{container_host('consul')}:8500"
  end

  def wait_for_consul
    30.times do
      begin
        Diplomat::Datacenter.get
        puts "consul up and running"
        return
      rescue => e
        print "."
      end
      sleep 1
    end
  end


  def init_consul
    Diplomat.configure do |config|
      config.url = consul_address
      config.acl_token = CONSUL_MASTER_TOKEN
    end

    # Diplomat::Acl.create(ID: 'allow_hivemq_discovery', Type: 'client', Rules: 'service "hivemq-service-discovery" { policy = "write" }')
    # puts Diplomat::Acl.list
  end

end

RSpec.configure do |c|
  c.wait_timeout = 30
  c.include Helpers
  c.extend Helpers
end