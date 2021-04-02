# Camel-K Aggregate EIP with HazelcastAggregationRepository

## Hazelcast setup

Download and install the Hazelcast Helm chart

```bash
helm repo add hazelcast https://hazelcast-charts.s3.amazonaws.com/
helm repo update
helm install my-release hazelcast/hazelcast
```

Apply the RBAC for the integrations to be able to fetch the network info from the k8s cluster

```bash
kubectl apply -f https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml
```

## Integrations

The first integration will read a CSV file from gist.github.com and stream the rows to a Knative Event broker.

The second integration will listen for the knative events, aggregate and log the new body. We will first launch and scale the second integration with 5 replicas

```bash
kamel run AggregationRoute.java

kubectl scale it aggregation-route --replicas 5
```

```bash
$ kubectl get po
NAME                                                  READY   STATUS             RESTARTS   AGE
aggregation-route-00001-deployment-54c46b68b7-2xtds   2/2     Running            0          19s
aggregation-route-00001-deployment-54c46b68b7-crbgq   2/2     Running            0          46s
aggregation-route-00002-deployment-d46865898-2ptw8    1/2     Running            1          30s
aggregation-route-00002-deployment-d46865898-2vs84    1/2     Running            1          30s
aggregation-route-00002-deployment-d46865898-nggfb    1/2     Running            1          30s
aggregation-route-00002-deployment-d46865898-vdzmr    2/2     Running            0          31s
aggregation-route-00002-deployment-d46865898-zz6vg    1/2     Running            1          30s
```

Then we will start the csv reader : 

```bash
kubectl apply -f csv-source.integration.yml
```

Let's log the aggregation route : 

```bash
kamel log aggregation-route
```

As we can see, messages are aggregated correctly on `entity_id` field : 

```log
2021-04-02 08:56:32,254 INFO  [route1] (Camel (camel-1) thread #1 - AggregateTimeoutChecker) [{entity_id=--gFCQWCcZ_pnleMRUExEA, i=1, type=company, label=GROOVY, LLC, label_en=, num_documents=1, sanctioned=false, pep=false, degree=2, source_counts={"USA/az_corporate_registry":1.0}, edge_counts={"registered_agent_of":{"out":0,"in":1,"total":1},"manager_of":{"out":0,"in":1,"total":1}}, additional_information=, address=, business_purpose=, company_type=, contact=, country=, date_of_birth=, finances=, gender=, identifier=, name=, person_status=, position=, shares=, status={"date":"2013-02-27","value":"registered"}, translated_name=, weak_identifier=}, {entity_id=--gFCQWCcZ_pnleMRUExEA, i=0, type=company, label=GROOVY, LLC, label_en=, num_documents=1, sanctioned=false, pep=false, degree=2, source_counts={"USA/az_corporate_registry":1.0}, edge_counts={"registered_agent_of":{"out":0,"in":1,"total":1},"manager_of":{"out":0,"in":1,"total":1}}, additional_information=, address={"value":"13632 N 58TH PL, SCOTTSDALE, Maricopa, AZ, 85254","house_number":"13632","road":"N 58th Pl","city":"SCOTTSDALE","state_district":"Maricopa","state":"AZ","postcode":"85254","category":"Known Place of Business"}, business_purpose=, company_type={"value":"Domestic LLC"}, contact=, country={"value":"USA","context":"domicile","state":"US-AZ"}, date_of_birth=, finances=, gender=, identifier={"value":"L18267408","type":"usa_az_corp_reg_entity_num"}, name={"value":"GROOVY, LLC","context":"primary"}, person_status=, position=, shares=, status={"date":"2013-02-20","value":"incorporated"}, translated_name=, weak_identifier=}, {entity_id=--gFCQWCcZ_pnleMRUExEA, i=2, type=company, label=GROOVY, LLC, label_en=, num_documents=1, sanctioned=false, pep=false, degree=2, source_counts={"USA/az_corporate_registry":1.0}, edge_counts={"registered_agent_of":{"out":0,"in":1,"total":1},"manager_of":{"out":0,"in":1,"total":1}}, additional_information=, address=, business_purpose=, company_type=, contact=, country=, date_of_birth=, finances=, gender=, identifier=, name=, person_status=, position=, shares=, status={"value":"active","text":"Active"}, translated_name=, weak_identifier=}]
```