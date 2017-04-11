#!/bin/bash
sudo service codedeploy-agent restart
yum -y install httpd > /var/log/installapache.out 2>&1

