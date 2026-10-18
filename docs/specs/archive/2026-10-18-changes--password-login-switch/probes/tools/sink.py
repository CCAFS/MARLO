#!/usr/bin/env python3
# CHG-PWD-SWITCH-001-T08 -- the local SMTP sink of the probes: accepts every message on 127.0.0.1:2525 and appends it to an mbox.
# The first line of each message is "From sink <date> -> <rcpt> <rcpt>", the form CHG-OTP-LOGIN-001 ac-probe.sh parses to read
# the SMTP envelope (a plain "From sink <date>" line, with the recipients in a header, leaves its envelope check empty).
# Usage: sink.py <mbox file> [port]
import datetime, socketserver, sys

MBOX = sys.argv[1]
PORT = int(sys.argv[2]) if len(sys.argv) > 2 else 2525


class Handler(socketserver.StreamRequestHandler):
    def handle(self):
        def w(s):
            self.wfile.write((s + "\r\n").encode())
        w("220 sink ESMTP")
        rcpts = []
        while True:
            line = self.rfile.readline()
            if not line:
                return
            c = line.decode(errors="replace").strip()
            u = c.upper()
            if u.startswith("EHLO"):
                w("250-sink")
                w("250 8BITMIME")
            elif u.startswith("HELO"):
                w("250 sink")
            elif u.startswith("MAIL"):
                rcpts = []
                w("250 OK")
            elif u.startswith("RCPT"):
                rcpts.append(c[8:].strip())
                w("250 OK")
            elif u.startswith("DATA"):
                w("354 go")
                buf = []
                while True:
                    l = self.rfile.readline()
                    if l in (b".\r\n", b".\n", b""):
                        break
                    buf.append(l[1:] if l.startswith(b"..") else l)
                with open(MBOX, "ab") as f:
                    f.write(("From sink %s -> %s\n" % (datetime.datetime.now().strftime("%a %b %d %H:%M:%S %Y"), " ".join(rcpts))).encode())
                    f.write(b"".join(buf).replace(b"\r\n", b"\n"))
                w("250 OK")
            elif u.startswith("QUIT"):
                w("221 bye")
                return
            else:
                w("250 OK")


class Server(socketserver.ThreadingTCPServer):
    allow_reuse_address = True


Server(("127.0.0.1", PORT), Handler).serve_forever()
