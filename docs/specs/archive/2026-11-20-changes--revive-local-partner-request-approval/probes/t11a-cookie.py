import sys,os
src=open('<home>/Documents/MARLO-PROJECT/GitHub/MARLO/docs/specs/changes/revive-local-partner-request-approval/probes/t11a-probe.py').read().split('if __name__')[0]
exec(src)
who=sys.argv[1]; crp=sys.argv[2] if len(sys.argv)>2 else "AICCRA"
print(login(who,crp).split("=",1)[1])
