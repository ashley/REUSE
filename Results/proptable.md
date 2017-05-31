### RxJava Pull Request
Pull | Entropy | CE
---| --- | ---
5246_b | -11110.4707 | -3.7739
5246_f | -12369.3619 | -3.9734
5250_b | -21840.0326 | -3.0203
5250_f | -21932.3870 | -3.0193

BugFix | Changes | CE-Left | CE-Right | CE-Avg | CE-Diff
| --- | ---: | ---: | ---: | ---: | ---: |
5246 | -19099.5118 | -6.4876 | -6.1354 | -4.2443 | -0.3522
5250 | -60431.2964 | -8.3572 | -8.3192 | -5.5630 | -0.0379

### Test2.java
BugFix | Changes | CE-Left | CE-Right | CE-Avg | CE-Diff
---: | ---: | ---: | ---: | ---: | ---:
before-to-before | -37.9588 | -4.7448 | -4.7448 | -3.1632 | 0.0
before-to-after | -37.9588 | -4.7448 | -5.4226 | -3.4508 | 0.6778
after-to-after | -37.8848 | -5.4121 | -5.4121 | -3.7884 | 0.0

### TestNoComments.java
BugFix | Changes | CE-Left | CE-Right | CE-Avg | CE-Diff
---: | ---: | ---: | ---: | ---: | ---:
before-to-before | -1864.9178 | -8.4768 | -8.4768 | -5.6512 | 0.0
before-to-after | -1864.9178 | -8.4768 | -8.6740 | -5.7031 | 0.1971
after-to-after | -1786.5376 | -8.3094 | -8.3094 | -5.5482 | 0.0

### Defects4j Buggy/Fixed Files

TypeOfBug | Changes | CE-Left | CE-Right | CE-Avg | CE-Diff | Expected | Observed | Notes
---: | ---: | ---: | ---: | ---: | ---: | --- | --- | --- 
unn_1 | -380.4179 | -5.0054 | -5.5133 | -3.4583 | 0.5078 | Negative | Equal | Wrong argument type
unn_2 | -593.4720 | -6.5941 | -7.2374 | -4.5303 | 0.6433 | Negative | Positive | Asks for a return on a void function 
unn_3 | -395.8753 | -6.7097 | -5.4982 | -4.1671 | -1.2114 | Negative | Negative | Fixed by throwing exception
nat_1 | -444.1586 | -6.4370 | -6.4370 | -4.3122- | 0.0000 | Equal | Equal | Changed argument value
not_1 | -517.9211 | -6.8147 | -7.0948 | -4.6242 | 0.2800 | Positive | Positive | Change argument type
not_2 | -531.1510 | -6.1051 | -6.1761 | -4.0857 | 0.0709 | Positive | Positive | Initializing unknown method in f
not_3 | -395.9493 | -6.4909 | -6.4909 | -4.3510 | 0.0000 | Equal | Equal | Nothing changed
not_4 | -531.1510 | -6.1051 | -6.3994 | -4.1496 | -0.2145 | Negative | Negative | Delete unknown method in f
na_1 |  -199.5726 | -6.6524 | -4.6412 | -3.9131 | -2.0111 | Negative | Positive | Create a method for unknkown method from b in f
na_2 | -369.2301 | -5.6804 | -5.8607 | -3.8461 |  0.1803 | Negative | Positive | OOB Error
