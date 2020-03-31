#RabbitMq安装使用说明
## 服务器信息
>CentOS Linux release 7.7.1908 (Core)

## 安装Erlang
`curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash`

`yum install erlang `

## 安装RabbitMQ
`rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc`

`wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.8.3/rabbitmq-server-3.8.3-1.el7.noarch.rpm`

` yum install rabbitmq-server-3.8.3-1.el7.noarch.rpm `

> 官网下载太慢可以使用以下下载地址
  https://bintray.com/rabbitmq/rpm/rabbitmq-server
  如果出现安装出错，从新下载再次安装
  
1. 启停MQ

    ```
    service rabbitmq-server start
    service rabbitmq-server status
    service rabbitmq-server stop
    ```

2. 设置开机启动

    `chkconfig rabbitmq-server on`

3. 开启web管理
    ```
    执行命令：
     rabbitmq-plugins enable rabbitmq_management
     service rabbitmq-service restart
    ```
    浏览器访问：[管理端地址](http://127.0.0.1:15672) 
    ```
    默认账号：guest/guest 
    ```
    > 注意：
    登录提示 User can only log in via localhost 解决方法：
    找到这个文件rabbit.app
    /usr/lib/rabbitmq/lib/rabbitmq_server-3.8.3/ebin/rabbit.app
    将：{loopback_users, [<<”guest”>>]}，
    改为：{loopback_users, []}，然后重启服务
    原因：rabbitmq从3.3.0开始禁止使用guest/guest权限通过除localhost外的访问
    


