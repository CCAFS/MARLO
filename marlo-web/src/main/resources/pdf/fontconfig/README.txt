Fontconfig rules for the local PDF engine (CHG-LPRE-T13)
=========================================================

PhantomJS 2.1.1 lays text out through Qt and fontconfig. The reports microservice runs it in a Debian bookworm
image, where fontconfig's default rules (hinting, antialiasing, LCD filter, the unhinted small DejaVu Sans rule,
family preferences) change glyph metrics and therefore page breaks. A minimal configuration does not reproduce them
(measured: 103 words on page 1 of the project fixture against 49 in production; 480 pages against 481 for the
real cluster report). These files are that rule set, so that local renders match production.

Source
  - conf.d/*.conf: /etc/fonts/conf.d of Debian bookworm, package fontconfig-config 2.14.1-4, together with the
    conf files of fonts-dejavu-core, symlinks resolved, copied unchanged. 50-user.conf and 51-local.conf are left
    out on purpose: they only add the user's own font directories, and the engine must see the bundled fonts only.
  - fonts.conf.template: /etc/fonts/fonts.conf of the same package, with its <dir> and <cachedir> entries
    replaced by the placeholders @FONT_DIR@, @CONF_DIR@ and @CACHE_DIR@, which PhantomJsRenderer fills with the
    private directories of the render engine (the bundled DejaVu fonts, the extracted conf.d, a private cache).
  - conf.list: the files of conf.d, read by PhantomJsRenderer (a classpath directory cannot be listed from a jar).

Licence: see LICENSE-fontconfig.txt (copyright file of fontconfig-config as shipped by Debian).
