FROM anapsix/alpine-java

ENV HIVEMQ_VERSION="3.3.2" \
    HIVEMQ_CHECKSUM="4d77867c25053b72a532303f643fdbf99270672732f413a3ccef96d84603601b"

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
COPY hivemq-consul-cluster-discovery-SNAPSHOT-all.jar /opt/hivemq/plugins
EXPOSE 1883
EXPOSE 9080

WORKDIR /opt/hivemq
USER hivemq

CMD ["/opt/hivemq/bin/run.sh"]

