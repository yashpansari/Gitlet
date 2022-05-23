import re

txt = r""" 1. # Create two branches and merge where second merge parents must be followed.
1. # Standard commands and definitions
2. > init
3. <<<
4. D DATE "Date: \w\w\w \w\w\w \d+ \d\d:\d\d:\d\d \d\d\d\d [-+]\d\d\d\d"
5. # A status log header RE.  Captures the commit id in its sole group.
6. D COMMIT_HEAD "commit ([a-f0-9]+)[ \t]*\n(?:Merge:\s+[0-9a-f]{7}\s+[0-9a-f]{7}[ ]*\n)?${DATE}"
7. # A full log entry.  Captures the entry. Assume logs messages don't contain
8. # "==="
9. D COMMIT_LOG "(===[ ]*\ncommit [a-f0-9]+[ ]*\n(?:Merge:\s+[0-9a-f]{7}\s+[0-9a-f]{7}[ ]*\n)?${DATE}[ ]*\n(?:.|\n)*?(?=\Z|\n===))"
10. # An arbitrary line of text (works even with ?s)
11. D ARBLINE "[^\n]*(?=\n|\Z)"
12. # Zero or more arbitrary full lines of text.
13. D ARBLINES "(?:(?:.|\n)*(?:\n|\Z)|\A|\Z)"
3. > branch B1
 4. <<<
 5. > branch B2
 6. <<<
 7. > checkout B1
 8. <<<
 9. + h.txt wug.txt
10. > add h.txt
11. <<<
12. > commit "Add h.txt"
13. <<<
14. > checkout B2
15. <<<
16. + f.txt wug.txt
17. > add f.txt
18. <<<
19. > commit "f.txt added"
20. <<<
21. > branch C1
22. <<<
23. + g.txt notwug.txt
24. > add g.txt
25. <<<
26. > rm f.txt
27. <<<
28. > commit "g.txt added, f.txt removed"
29. <<<
30. = g.txt notwug.txt
31. * f.txt
32. * h.txt
33. > checkout B1
34. <<<
35. = h.txt wug.txt
36. * f.txt
37. * g.txt
38. > merge C1
39. <<<
40. = f.txt wug.txt
41. = h.txt wug.txt
42. * g.txt
43. > merge B2
44. <<<
45. * f.txt
46. = g.txt notwug.txt
47. = h.txt wug.txt"""
txt = "".join(re.split("\n", txt))
x = "\n".join(re.split("[0-9]+. ", txt))
text_file = open("./samples/setup2.txt", "r")
data = text_file.read()
text_file.close()
x = re.sub('I setup2.inc', data, x)
text_file = open("./samples/setup1.txt", "r")
data = text_file.read()
text_file.close()
x = re.sub('I setup1.inc', data, x)
text_file = open("./samples/prelude.txt", "r")
data = text_file.read()
text_file.close()
text_file = open("./samples/blank-status2.txt", "r")
data = text_file.read()
text_file.close()
x = re.sub('I blank-status2.inc', data, x)
print(x)
