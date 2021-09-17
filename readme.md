## PPM Password Manager
### Features
- tree-like structure of containers with notes, passwords and files
- edit tree structure by drag and drop
- notes, passwords and files are encrypted in database
- users and groups management
- allowed IP for users
- access to container management
- additional layer of transport encryption
- password generator
- search
- actions logging
- IP blacklist
- IP whitelist
- dynamic blocking for incorrect login attempts

### Installation
Prerequisites:
+ JRE 1.8+
+ MySQL 8
+ MySQL database created
+ MySQL database collation utf8_general_ci
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
  <key_alias> shows following command:\
  `keytool -list -v -keystore cert.pfx`
+ edit my.ini or my.cnf and set max_allowed_packet=80M

Running:\
`java -jar ppm-1.0xx.jar`

Launch:\
Go to `https://<server_name>:<port>`

Login: admin
\
Password: admin

After first run you need to generate and save database encryption key.\
Go __Admin__ -> __Database__ and click "__Generate__", then save the key.\
You will have to input the key after the server rebooted.\
You will lose your encrypted data if the key is lost.

### Register as linux service
1. create system user\
    `sudo useradd -r ppm`
2. if you want to use privileged ports\
    `sudo setcap 'cap_net_bind_service=+ep' /usr/lib/jvm/default-java/bin/java`
3. create service\
    `/etc/systemd/system/ppm.service`
   ```
    [Unit]
    Description=PPM service
    After=mysql.service
    StartLimitBurst=2
    StartLimitIntervalSec=1

    [Service]
    Type=simple
    Restart=no
    RestartSec=1
    User=ppm
    ExecStart=/usr/bin/env java -jar /usr/local/ppm/ppm-1.001.jar
    WorkingDirectory=/usr/local/ppm
    SyslogIdentifier=ppm

    [Install]
    WantedBy=multi-user.target
   ```
4. allow autostart `sudo systemctl enable ppm`
5. configure syslog
    + create file `/var/log/ppm.log`
    + `sudo chown syslog:adm /var/log/ppm.log`
    + create file `/etc/rsyslog.d/ppm.conf`
        ```
        :programname, isequal, "ppm" /var/log/ppm.log
        & stop
        ```
    + `sudo service rsyslog restart`
6. start service `sudo service ppm start`

### Prepare
- create users
- create groups
- add users to groups
- create containers
- grant access for groups to containers

### Access
Group with __Admin__ flag have full access to all containers and included objects regardless of access settings.

__NA__ - No Access. Do not have any access to container, subcontainers and included objects and cannot see it.\
__PT__ - Path Through. Can see only container name and navigate to subcontainers with granted access.\
__RO__ - Read Only. Can read all data from container.\
__RW__ - Read Write. Full access.

###Changelog
- __1.002__
  + Generated password can be deleted
  + Container can be opened by double click
- __1.003__
  + visual improvements
  + //TODO rework opening item
  + //TODO change password at next logon