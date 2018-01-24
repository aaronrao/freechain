# freechain
Freechain - һ���򵥵�������ʵ�֣�������������һЩ�������ԣ���ȥ���Ļ���P2PͨѶ�����ף� �ڿ󣬹�ʶ�㷨��

### Quick start
```
git clone https://github.com/aaronrao/freechain.git
cd freechain
mvn clean install
java -jar freechain.jar 5000
java -jar freechain.jar 5001

��ֱ��������������Main��������P2P�ڵ�(5000�˿ں�5001�˿�)����������˿ڲ���

```


### HTTP API

- ��ѯ������

  ```
  curl http://localhost:5000/chain

  ```
- �ڿ�

  ```
  curl http://localhost:5000/mine

  ```

- ����

  ```
  curl -H "Content-type:application/json" --data 
  '{"sender": "d4e44223434sdfdgerewfd3fefe9dfe","recipient": "45adiy5grt4544sdfdg454efe54dssq5","amount": 1}' 
  http://localhost:5000/transactions/new

  ```

- �ڵ�ע��

  ```
  curl -H "Content-type:application/json" --data '{"urls" : "localhost:5000,..."}' http://localhost:5001/peers/register

  ```

- �滻��ʶ����

  ```
  curl http://localhost:5001/peers/resolve
  ```