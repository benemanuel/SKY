# Use an official lightweight Nginx image
FROM nginx:alpine

# Copy all your project files into the Nginx default web root directory
COPY . /usr/share/nginx/html

# Expose port 80, the default for Nginx
EXPOSE 80