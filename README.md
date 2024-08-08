# docker-liteloaderqqnt

基于 [sgpublic/docker-linuxqq](https://github.com/sgpublic/docker-linuxqq) 构建。

## 食用方法

```yaml
version: '3.1'
services:
  linuxqq:
    image: mhmzx/docker-liteloaderqqnt:v1.2.0
    restart: unless-stopped
    ports:
      - 5800:5800
    volumes:
      - ./config:/home/linuxqq/config
      - ./config/baseimage-gui:/config # 上游项目的配置目录
```

支持修改的环境变量：

| 环境变量                        | 描述	                                                       | 默认值（留空则表示必填）                       |
|-----------------------------|-----------------------------------------------------------|------------------------------------|
| USER_ID                     | 运行 QQ 所使用的用户 ID                                           | 1000                               |
| GROUP_ID                    | 运行 QQ 所使用的用户组 ID                                          | 1000                               |
| KEEP_APP_RUNNING            | 保持应用运行，当 QQ 意外停止时自动重启，`1` 表示开启                            | 0                                  |
| SECURE_CONNECTION           | 为 noVNC 启用 HTTPS 访问                                       | 0                                  |
| WEB_LISTENING_PORT          | noVNC 端口                                                  | 5800                               |
| WEB_PATH_PREFIX             | （暂未支持，等待上游更新）网页 URL 前缀，若填写需以 `/` 开头，不能以 `/` 结尾，留空则不开启前缀   | （默认为空字符串）                          |
| WEB_AUTHENTICATION          | 为 noVNC 启用认证保护，需开启 `SECURE_CONNECTION` 才能开启               | 0                                  |
| WEB_AUTHENTICATION_USERNAME | noVNC 认证用户名                                               | （若启用 `WEB_AUTHENTICATION` 则必填）     |
| WEB_AUTHENTICATION_PASSWORD | noVNC 认证密码                                                | （若启用 `WEB_AUTHENTICATION` 则必填）     |
| XDG_CONFIG_HOME             | 配置文件目录，包括 QQ 产生的文件                                        | /home/linuxqq/config               |
| QQ_HOME                     | QQ 安装目录，若想使用外部 QQ 安装实例可修改此环境变量或直接挂载 /opt/QQ               | /opt/QQ                            |
| LITELOADERQQNT_PROFILE      | LiteLoaderQQNT 数据文件目录                                     | $XDG_CONFIG_HOME/LiteLoaderProfile |
| LITELOADERQQNT_HOME         | LiteLoaderQQNT 安装目录，若需使用外部 LiteLoaderQQNT 安装实例可修改此目录或直接挂载 | $XDG_CONFIG_HOME/LiteLoaderQQNT    |

其他环境变量请参阅：[jlesage/docker-baseimage-gui#environment-variables](https://github.com/jlesage/docker-baseimage-gui?tab=readme-ov-file#environment-variables)
