## CSV Streaming Camel-K Integration Example

This integration reads a CSV file, produces a stream line by line with a map representing each row mapped to headers.

### Run the integration

```bash
kubectl apply -f csv-streaming.yml
```

### Log

```bash
kamel log csv-streaming.yml
```

