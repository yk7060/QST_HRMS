FROM nginx:alpine

COPY dist/ /usr/share/nginx/html

RUN chmod -R 777 /usr/share/nginx/html

COPY default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
