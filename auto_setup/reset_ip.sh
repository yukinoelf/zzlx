#!/bin/bash
# author: amirline

#�޸�im_server/conf-a�е������ļ�
#ֻ���������з��񶼲�����һ̨�����ϡ�

FILE_SERVER_CONF=fileserver.conf
LOGIN_SERVER_CONF=loginserver.conf
MSG_SERVER_CONF=msgserver.conf
ROUTE_SERVER_CONF=routeserver.conf
MSFS_SERVER_CONF=msfs.conf
HTTP_MSG_SERVER_CONF=httpmsgserver.conf
PUSH_SERVER_CONF=pushserver.conf
DB_PROXY_SERVER_CONF=dbproxyserver.conf

FILE_SERVER=file_server
LOGIN_SERVER=login_server
MSG_SERVER=msg_server
ROUTE_SERVER=route_server
MSFS_SERVER=msfs
HTTP_MSG_SERVER=http_msg_server
PUSH_SERVER=push_server
DB_PROXY_SERVER=db_proxy_server


echo The old server ip: $1
echo The new server ip: $2
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$FILE_SERVER/$FILE_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$LOGIN_SERVER/$LOGIN_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$MSG_SERVER/$MSG_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$ROUTE_SERVER/$ROUTE_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$MSFS_SERVER/$MSFS_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$HTTP_MSG_SERVER/$HTTP_MSG_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$PUSH_SERVER/$PUSH_SERVER_CONF
sed -i "s/$1/$2/g"  ./im_server/im-server-1.0/$DB_PROXY_SERVER/$DB_PROXY_SERVER_CONF