#!/usr/bin/env bash

# Set the IP of a server/cluster to joint here
CONSUL_SERVER_IP_ADDRESS=""

# Gets the private ip of the server automatically
PRIVATE_IP_ADDRESS=$(curl http://169.254.169.254/latest/meta-data/local-ipv4)

# Latest version doesn't work with Spring Consul yet
CONSUL_VERSION=0.9.3

# =======================
# Install all the packages
# =======================
yum update -y && yum install -y \
    systemd-networkd.x86_64 \
    dnsmasq


# =======================
# Install docker compose (because it's useful for dev)
# =======================
curl -L https://github.com/docker/compose/releases/download/1.18.0-rc2/docker-compose-`uname -s`-`uname -m` | sudo tee /usr/local/bin/docker-compose > /dev/null
chmod +x /usr/local/bin/docker-compose


# =======================
# Install Consul
# =======================
wget https://releases.hashicorp.com/consul/${CONSUL_VERSION}/consul_${CONSUL_VERSION}_linux_amd64.zip
unzip consul_${CONSUL_VERSION}_linux_amd64.zip
mv consul /usr/bin/


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
# Configure dnsmasq
# =======================
cat <<EOF > /etc/dnsmasq.d/consul.conf
server=/consul/169.254.1.1#8600
listen-address=127.0.0.1
listen-address=169.254.1.1
EOF

systemctl restart dnsmasq


# =======================
# Configure system environment variables
# =======================
cat <<EOF > /etc/environment
CONSUL_HTTP_ADDR=169.254.1.1:8500
CONSUL_RPC_ADDR=169.254.1.1:8400
EOF


# =======================
# Configure consul
# =======================
mkdir -p /etc/consul.d/

cat <<EOF > /etc/consul.d/config.json
{
    "server": false,
    "datacenter": "ap2",
    "start_join": ["${CONSUL_SERVER_IP_ADDRESS}"],
    "data_dir": "/var/consul"
}
EOF

cat <<EOF > /etc/consul.d/interfaces.json
{
  "client_addr": "169.254.1.1",
  "bind_addr": "${PRIVATE_IP_ADDRESS}"
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
# Install docker last for DNS issues.
# =======================
yum install -y \
    docker

usermod -a -G docker ec2-user
service docker start


# =======================
# Done, so output Consul logs
# =======================
journalctl -fu consul