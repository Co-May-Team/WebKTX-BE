FROM anapsix/alpine-java
MAINTAINER devwebbe
COPY /target/webktx-1.0-SNAPSHOT.jar /home/webktx-1.0-SNAPSHOT.jar
CMD ["java","-jar","/home/webktx-1.0-SNAPSHOT.jar"]