FROM adoptopenjdk/openjdk11:ubi

ADD target/techchallenge-*.jar app.jar

RUN bash -c 'touch /app.jar' && \
    bash -c 'mkdir /logs/'

CMD java -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs -XX:+PrintGC -Xmx3000m -Dserver.port=$PORT -Dspring.datasource.url=$DATABASE -Dspring.datasource.username=$DB_USERNAME -Dspring.datasource.password=$DB_PASSWORD -jar /app.jar
