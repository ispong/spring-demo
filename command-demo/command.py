import os
import sys

# 执行所有本地命令
command = sys.argv[1]
print(os.system(command))