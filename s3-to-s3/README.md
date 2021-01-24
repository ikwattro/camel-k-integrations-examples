## CSV on S3 to Json Files on S3

This integration converts a CSV file containing articles ( access to CSV not provided ), stream each row and produce a new json file on another s3 bucket path, one file for each row.

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

This will create a named `s3-to-s3-configmap` ConfigMap.

### Run the integration

```bash
kubectl apply -f integration.yml
```




