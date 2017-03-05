FROM anapsix/alpine-java

ENV HIVEMQ_VERSION="3.2.3" \
    HIVEMQ_CHECKSUM="1f7eb01ceb838a63f89d5d84b41e0ef74c06a1f36125fd01ade3ee5241748052"

ADD https://hivemq.com/releases-all/hivemq-${HIVEMQ_VERSION}.zip /opt/hivemq-${HIVEMQ_VERSION}.zip

RUN echo "${HIVEMQ_CHECKSUM}  /opt/hivemq-${HIVEMQ_VERSION}.zip" | sha256sum -c && \
  unzip /opt/hivemq-${HIVEMQ_VERSION}.zip -d /opt && \
  ln -s /opt/hivemq-${HIVEMQ_VERSION} /opt/hivemq && \
  addgroup -S hivemq && \
  adduser -S -G hivemq -h /opt/hivemq -s /bin/bash hivemq && \
  chown -R hivemq:hivemq /opt/hivemq /opt/hivemq-${HIVEMQ_VERSION}

RUN chmod +x /opt/hivemq/bin/run.sh

COPY config.xml /opt/hivemq/conf/config.xml
COPY logback.xml /opt/hivemq/conf/logback.xml
COPY hivemq-consul-cluster-discovery-0.0.1-SNAPSHOT-all.jar /opt/hivemq/plugins
EXPOSE 1883

WORKDIR /opt/hivemq
USER hivemq

CMD ["/opt/hivemq/bin/run.sh"]

