### Odd-Code Dataset

Repo Name | ID | Number of Bugs 
--- | --- | ---: 
atmosphere | atmosphere/atmosphere | 5,799
derby | apache/derby | 9,717 | 
elasticsearch | elastic/elasticsearch | 10,389
facebook-android-sdk | facebook/facebook-android-sdk | 741
lucene-solr | apache/lucene-solr | 21,850
netty | netty/netty | 8,745
openjpa | apache/openjpa | 3,202
presto | prestodb/presto | 2,964
qpid | apache/qpid | 9,012
wicket | apache/wicket | 20,131

### Atmosphere
Bad Commits: 3361
10-fold cross validation
Fold commits: 580

K-Fold | Training | Testing
--- | --- | ---
1 | 1-5220 | 5220-5799
2 | 1-4640 5221-5799 | 4641-5220
3 | 1-4060 4641-5799 | 4061-4640
4 | 1-3480 4061-5799 | 3481-4060 *
5 | 1-2900 3481-5799 | 2901-3480 
6 | 1-2320 2901-5799 | 2321-2900
7 | 1-1740 2321-5799 | 1741-2330 
8 | 1-1160 1741-5799 | 1161-1740
9 | 1-580 1161-5799 | 581-1160
10 | 581-5799 | 1-580 

### Derby
10-fold cross validation
Fold commits: 971

K-Fold | Training | Testing
--- | --- | ---
1 | 1-8739 | 8740-9710
2 | 1-7768 8740-9710 | 7769-8739
3 | 1-6797 7769-9710 | 6798-7768
4 | 1-5826 6798-9710 | 5827-6797 
5 | 1-4855 5827-9710 | 4856-5828 
6 | 1-3884 4856-9710 | 2885-4857 *
7 | 1-2913 3885-9710 | 2914-3884
8 | 1-1942 2914-9710 | 1943-2913
9 | 1-971 1943-9710 | 972-1942
10 | 972-9710 | 1-971 


### Elastic Search
10 fold cross validation
Fold commits: 889

K-Fold | Training | Testing
--- | --- | ---
1 | 1-8001 | 8002-8890
2 | 1-7112 8001-8890 | 7113-8000
3 | 1-6223 7112-8890 | 6224-7111
4 | 1-5334 6223-8890 | 5333-6222
5 | 1-4445 5334-8890 | 4446-5334
6 | 1-3556 4445-8890 | 3557-4444
7 | 1-2667 3556-8890 | 2668-3555
8 | 1-1778 2667-8890 | 1779-2666
9 | 1-889 1778-8890 | 890-1777
10 | 889-8890 | 1-888


### Open JPA
10 fold cross validation
Fold commits : 320

K-Fold | Training | Testing
--- | --- | ---
1 | 1-2880 | 2881-3200 
2 | 1-2560 2881-3200 | 2561-2880
3 | 1-2240 2561-3200 | 2241-2560
4 | 1-1920 2241-3200 | 1921-2240
5 | 1-1600 1921-3200 | 1601-1920
6 | 1-1280 1601-3200 | 1281-1600
7 | 1-960 1281-3200 | 961-1280
8 | 1-640 961-3200 | 641-960
9 | 1-320 641-3200 | 321-640
10 | 320-3200 | 1-321

### Netty
10 fold cross validation
Fold commits: 874

K-Fold | Training | Testing
-- | --- | ---
1 | 1-840 | 7867-8740
2 | 1-6992 7867-8740 | 6993-7866
3 | 1-6118 6993-8740 | 6118-6992
4 | 1-5244 6118-8740 | 5245-6117
5 | 1-4370 5245-8740 | 4371-5244
6 | 1-3496 4371-8740 | 3497-4370
7 | 1-2622 3497-8740 | 2623-3496
8 | 1-1748 2623-8740 | 1749-2622
9 | 1-874 1749-8740 | 875-1748
10 | 840-8740 | 1-841

### openjpa
Fold commits: 320
K-Fold | Training | Testing
--- | --- | ---
1 | 1-2880 | 2881-3200
2 | 1-2560 2881-3200 | 2561-2880
3 | 1-2240 2561-3200 | 2241-2560
4 | 1-1920 2241-3200 | 1921-2240
5 | 1-1600 1921-3200 | 1601-1920
6 | 1-1280 1601-3200 | 1281-1600
7 | 1-960 1281-3200 | 961-1280
8 | 1-640 961-3200 | 641-960
9 | 1-320 641-3200 | 321-640
10 | 321-3200 | 1-320

### Bugs from Jacob
```
7933	facebook/facebook-android-sdk	435	e1b016dd8e74baeef3bda04c05cc582643f26450	70886556	70886557	27e014de	1	Good	Java	maven
7933	facebook/facebook-android-sdk	435	7273977cfbd5b79014db9d6c4152144d0f19aa78	71220756	71220757	27e014de	1	Bad	Java	maven
7933	facebook/facebook-android-sdk	435	aa094b25021d0ed29b04c693c51dc9aa2f02bf59	71246017	71246018	27e014de	1	Good	Java	maven
```

### Files for Regression
project | filename | filePath | commitIDs
--- | --- | --- | ---
atmosphere | GlassFishWebSocketSupport.java | modules/cpr/src/main/java/org/atmosphere/container/GlassFishWebSocketSupport.java | [5264, 3493, 1370, 1408, 347, 4224, 5729, 1867, 399, 5544, 1012, 3581, 5744, 5272, 5264, 4568, 3425, 1584, 2150, 2726]
elasticsearch | SimpleChildQuerySearchTests.java | src/test/java/org/elasticsearch/test/integration/search/child/SimpleChildQuerySearchTests.java | [7576, 6101, 8218, 2249, 6410, 624, 8800, 1153]
derby | DataDictionaryImpl.java | java/engine/org/apache/derby/impl/sql/catalog/DataDictionaryImpl.java | [6816, 6000, 2694, 2560, 6127, 8684, 2036, 8884, 254, , 5778, 3567, 3422, 5739]
openjpa | DB2Dictionary.java | openjpa-jdbc/src/main/java/org/apache/openjpa/jdbc/sql/DB2Dictionary.java |[2194, 2349, 2063, 1395, 1100, 1141, 2835, 1264, 1805, 704, 3089]
netty | AbstractChannel.java | transport/src/main/java/io/netty/channel/AbstractChannel.java | [6931, 6540, 2048, 312, 8663, 2814, 3516, 1275, 2344, 5631, 5888, 8743, 2271, 5838, 2208, 6426, 7472, 6833, 5295, 7031, 6048, 5558]
