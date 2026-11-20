import sys,os
creds={}
for l in open(os.path.expanduser("~/.marlo-local-credentials")):
    if "=" in l and not l.startswith("#"):
        k,v=l.strip().split("=",1); creds[k]=v
who=sys.argv[1]; crp=sys.argv[2] if len(sys.argv)>2 else "AICCRA"
if who=="req":
    os.environ["PROBE_NONSA_USER"]="<requester username>"; os.environ["PROBE_NONSA_PW"]=creds["MARLO_LOCAL_PASSWORD"]; who="nonsa"
src=open('<repo>/docs/specs/changes/revive-local-partner-request-approval/probes/t11a-probe.py').read().split('if __name__')[0]
exec(src)
print(login(who,crp).split("=",1)[1])
