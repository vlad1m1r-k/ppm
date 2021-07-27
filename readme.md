## PPM Password Manager
### - Installation
Prerequisites:
+ JRE 1.8+
+ MySQL 8
+ MySQL database created
+ SSL certificate p12(pfx) format

Installation:
+ copy ppm-1.0xx.jar to server
+ copy you_certificate.p12 to program dir
+ create file application.properties in program dir
+ add settings to application.properties:
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/<DB_NAME>?useUnicode=true&characterEncoding=utf8&serverTimezone=Europe/Kiev
  spring.datasource.username=<db_username>
  spring.datasource.password=<db_password>
  server.ssl.key-store=keystore.p12
  server.ssl.key-store-password=<password>
  server.ssl.key-store-type=pkcs12
  server.ssl.key-alias=<key_alias>
  server.ssl.key-password=<password>
  server.port=8443
  ```
  <key_alias> shows following command:
  ```
  keytool -list -v -keystore cert.pfx
  ```
+ edit my.ini or my.cnf and set max_allowed_packet=80M

Running:
`java -jar ppm-1.0xx.jar`

Launch:
Go to https://<server_name>:<port>
Login: admin
Password: admin

After first run you need to generate and save database encryption key.
Go __Admin__ -> __Database__ and click "Generate", then save key.
You will have to input the key after the server rebooted.
You will lose your encrypted data if the key is lost.