require 'spec_helper'

describe 'hivemq consul discovery plugin', :extend_helpers do

  before(:all) do
    init_environment
  end

  after(:all) do
    cleanup_environment
  end

  def test_mqtt_connect(host, port)
    begin
      puts "probing mqtt #{host}:#{port}"
      return MQTT::Client.connect(host: host, port: port).connected?
    rescue MQTT::ProtocolException
      return false
    end
  end

  it 'hivemq_node1 can send a message to hivemq_node2' do
    host1, port1 = compose.address('hivemq_node1', 1883, index: 1)
    host2, port2 = compose.address('hivemq_node2', 1883, index: 1)

    # _, healthcheck_port1 = compose.address('hivemq_node1', 9080, index: 1)
    # _, healthcheck_port2 = compose.address('hivemq_node2', 9080, index: 1)
    #
    # puts "hivemq_node1 = #{host1}:#{port1} (healthcheck #{healthcheck_port1})"
    # puts "hivemq_node2 = #{host2}:#{port2} (healthcheck #{healthcheck_port2})"

    puts 'waiting for node 1'
    wait_for(port(port1)).to be_listening
    puts 'waiting for node 2'
    wait_for(port(port2)).to be_listening
    sleep 60

    client1 = MQTT::Client.connect(host: host1, port: port1)
    client2 = MQTT::Client.connect(host: host2, port: port2)

    client2.subscribe('cluster/test/topic')
    sleep 1
    client1.publish('cluster/test/topic', 'woop')

    wait_for { client2.get[1] }.to match(/woop/)

    client1.disconnect(false)
    client2.disconnect(false)
  end

end
