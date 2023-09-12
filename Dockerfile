FROM openjdk:17
WORKDIR /bank
COPY /build .
CMD ["java", "Main"]