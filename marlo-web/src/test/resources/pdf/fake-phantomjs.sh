#!/bin/sh
# A stand-in for the PhantomJS executable, for the tests of PhantomJsRenderer and LocalPdfReportService
# (CHG-LPRE-T13). The test copies this file with __REC__ replaced by a directory it owns, makes it executable, and
# writes the behaviour to run into $REC/mode. Everything the renderer passes in is recorded under $REC.
REC='__REC__'
MODE=$(cat "$REC/mode")

# CHG-PPE-T04: the modes libs-required and loader-fail stand in for a host without the font libraries. libs-required
# runs only when LD_LIBRARY_PATH names a directory holding all seven shipped libraries; otherwise, like loader-fail
# always, it prints the dynamic loader's message and exits 127.
LIBS="libfontconfig.so.1 libfreetype.so.6 libexpat.so.1 libpng16.so.16 libbrotlidec.so.1 libbrotlicommon.so.1 libz.so.1"
libs_present() {
  [ -n "$LD_LIBRARY_PATH" ] || return 1
  for lib in $LIBS; do
    [ -f "$LD_LIBRARY_PATH/$lib" ] || return 1
  done
  return 0
}
loader_error() {
  echo "$0: error while loading shared libraries: libfontconfig.so.1: cannot open shared object file: No such file or directory" >&2
  exit 127
}

if [ "$1" = "--version" ]; then
  env | sort > "$REC/env.version"
  printf '%s\n' "${LD_LIBRARY_PATH-unset}" >> "$REC/libpath.version"
  case "$MODE" in
    libs-required) libs_present || loader_error; echo 2.1.1; exit 0 ;;
    loader-fail) loader_error ;;
    version-fail)
      printf 'Auto configuration failed\001 first line\nSECOND-LINE-MARKER\n' >&2
      exit 3 ;;
    version-hang) sleep 60; exit 0 ;;
    version-other) echo 1.9.8; exit 0 ;;
    # The retry of the library probe cannot start: the first --version takes its own execute bit away and fails as a
    # host without the font libraries would, so the second --version is refused by the operating system.
    loader-fail-then-unstartable) chmod a-x "$0"; loader_error ;;
    # The retry of the library probe hangs: without LD_LIBRARY_PATH it fails as a host without the font libraries.
    libs-hang) [ -n "$LD_LIBRARY_PATH" ] || loader_error; sleep 60; exit 0 ;;
    *) echo 2.1.1; exit 0 ;;
  esac
fi

echo "$$" >> "$REC/started"
echo "$@" > "$REC/args.$$"
env | sort > "$REC/env.$$"
echo "$HOME" > "$REC/home.$$"
pwd > "$REC/pwd.$$"
if [ -n "$FONTCONFIG_FILE" ]; then cp "$FONTCONFIG_FILE" "$REC/fonts.conf.$$"; fi
if [ -f "$2" ]; then echo present > "$REC/script-present.$$"; fi

case "$MODE" in
  libs-required) libs_present || loader_error; MODE=ok ;;
  loader-fail) loader_error ;;
esac

if [ "$MODE" = "nostdin" ]; then
  sleep 300 &
  echo $! > "$REC/child.$$"
  wait
  exit 0
fi

cat > "$REC/stdin.$$"
FILE=$(sed -n 's/.*"filename":"\([^"]*\)".*/\1/p' "$REC/stdin.$$")

write_pdf() {
  printf '%%PDF-1.4\n1 0 obj\n<< /Type /Pages /Count 1 >>\nendobj\n2 0 obj\n<< /Type /Page >>\nendobj\n%%%%EOF\n' > "$FILE"
}

report() {
  printf '{"filename":"%s","blocked":%s,"failed":%s}' "$FILE" "$1" "$2"
}

case "$MODE" in
  ok)
    write_pdf
    report 2 1
    exit 0 ;;
  exit1)
    echo "SECRET-STDERR-MARKER" >&2
    exit 1 ;;
  exit1-long)
    head -c 500 /dev/zero | tr '\0' 'e' >&2
    printf '\001\nSECOND-LINE-MARKER\n' >&2
    exit 1 ;;
  noisy-stdout)
    write_pdf
    printf "SyntaxError: Unexpected token '}'\n\n  undefined:2 in setContent\n"
    report 0 0
    exit 0 ;;
  trailing-json)
    write_pdf
    report 0 0
    printf ' trailing text'
    exit 0 ;;
  nofile)
    report 0 0
    exit 0 ;;
  emptyfile)
    : > "$FILE"
    report 0 0
    exit 0 ;;
  notpdf)
    echo "hello" > "$FILE"
    report 0 0
    exit 0 ;;
  badjson)
    write_pdf
    printf 'SECRET-STDOUT-MARKER not json'
    exit 0 ;;
  flood-stdout)
    write_pdf
    head -c 200000 /dev/zero | tr '\0' 'x'
    exit 0 ;;
  flood-stderr)
    write_pdf
    head -c 3000000 /dev/zero | tr '\0' 'e' >&2
    report 0 0
    exit 0 ;;
  hang)
    sleep 300 &
    echo $! > "$REC/child.$$"
    wait
    exit 0 ;;
  *)
    echo "unknown mode $MODE" >&2
    exit 9 ;;
esac
