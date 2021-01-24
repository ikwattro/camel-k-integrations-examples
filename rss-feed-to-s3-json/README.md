## RSS Feed to Json File on S3 bucket

Poll an RSS Feed, and store the content as json file on an S3 bucket.

AWS Credentials are passed using k8s secrets and `application.properties` is configured using ConfigMaps.

### Create the AWS Secrets

Replace the `XXXX` with your credentials.

```bash
kubectl create secret generic aws \
--from-literal=ACCESS_KEY=XXXXXX \
--from-literal=SECRET_KEY=XXXXXX
```

### Create the configmap

```bash
kubectl apply -f config.yml
```

This will create a named `rss-to-s3-configmap` ConfigMap.

### Run the integration

```bash
kubectl apply -f rss-feed-to-s3.yml
```




