#!/usr/bin/env bash

# Remember to set where your configuration files are saved...
GIT_CONFIG_REPOSITORY=""

# Gets the private ip of the server automatically
PRIVATE_IP_ADDRESS=$(curl http://169.254.169.254/latest/meta-data/local-ipv4)

# =======================
# Install all the packages
# =======================
yum update -y && yum install -y \
    docker \
    git \
    systemd-networkd.x86_64 \
    dnsmasq


# =======================
# Install Consul
# =======================
wget https://releases.hashicorp.com/consul/1.0.1/consul_1.0.1_linux_amd64.zip
unzip consul_1.0.1_linux_amd64.zip
mv consul /usr/bin/
consul --version


# =======================
# Configure docker
# =======================
usermod -a -G docker ec2-user
service docker start


# =======================
# Configure dummy network
# =======================
mkdir -p /etc/systemd/network
cat <<EOF > /etc/systemd/network/dummy0.netdev
[NetDev]
Name=dummy0
Kind=dummy
EOF

cat <<EOF > /etc/systemd/network/dummy0.network
[Match]
Name=dummy0

[Network]
Address=169.254.1.1/32
EOF

systemctl daemon-reload
systemctl restart systemd-networkd


# =======================
# Configure consul
# =======================
mkdir -p /etc/consul.d/

cat <<EOF > /etc/consul.d/config.json
{
    "bootstrap": true,
    "server": true,
    "datacenter": "ap2",
    "data_dir": "/var/consul",
    "ui": true
}
EOF

cat <<EOF > /etc/consul.d/interfaces.json
{
  "client_addr": "169.254.1.1",
  "bind_addr": "${PRIVATE_IP_ADDRESS}"
}
EOF

cat <<EOF > /etc/consul.d/ui.json
{
  "addresses": {
    "http": "0.0.0.0"
  }
}
EOF

cat <<EOF > /etc/systemd/system/consul.service
[Unit]
Description=Consul
Documentation=https://www.consul.io/

[Service]
ExecStart=/usr/bin/consul agent -config-dir=/etc/consul.d/
ExecReload=/bin/kill -HUP $MAINPID
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl start consul.service


# =======================
# Configure dnsmasq
# =======================
cat <<EOF > /etc/dnsmasq.d/consul.conf
server=/consul/169.254.1.1#8600
listen-address=127.0.0.1
listen-address=169.254.1.1
EOF


# =======================
# Configure system environment variables
# =======================
cat <<EOF > /etc/environment
CONSUL_HTTP_ADDR=169.254.1.1:8500
CONSUL_RPC_ADDR=169.254.1.1:8400
EOF


# =======================
# Configure git2consul (docker image)
# =======================
mkdir -p /tmp/git2consul.d/
cat <<EOF > /tmp/git2consul.d/config.json
{
  "version": "1.0",
  "repos" : [{
    "name" : "swordfish",
    "url" : "${GIT_CONFIG_REPOSITORY}",
    "branches" : ["master"],
    "hooks": [{
      "type" : "polling",
      "interval" : "1"
    }]
  }]
}
EOF
docker run -d --name git2consul -v /tmp/git2consul.d:/etc/git2consul.d cimpress/git2consul --endpoint 169.254.1.1 --port 8500 --config-file /etc/git2consul.d/config.json
docker logs git2consul


# =======================
# Done, so output Consul logs
# =======================
journalctl -fu consul