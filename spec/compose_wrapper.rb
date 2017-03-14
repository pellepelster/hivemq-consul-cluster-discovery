require 'docker'
require 'docker/compose'

class ComposeWrapper < Docker::Compose::Session

  def initialize()
    super(dir: '.', file: File.join(File.dirname(__FILE__), 'docker-compose.yml'))
  end

  def docker_host
    URI(Docker.url).host || 'localhost'
  end

  def address(service, port, protocol: 'tcp', index: 1)
    mapped_port = port(
      service, port,
      protocol: protocol,
      index: index
    )
    _, port = mapped_port.split(':')
    [docker_host, Integer(port)]
  end

end
