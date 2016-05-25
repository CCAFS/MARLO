module.exports = function(grunt) {

  grunt.loadNpmTasks('grunt-wiredep');
  
  grunt.initConfig({
    wiredep: {
      task: {
        src: ['src/main/webapp/WEB-INF/global/pages/bowerComponents.ftl'],
        ignorePath: '../../../',
        options: {
          fileTypes: {
            ftl: {
              block: /(([ \t]*)\[#--\s*bower:*(\S*)\s*--])(\n|\r|.)*?(\[#--\s*endbower\s*--])/gi,
              detect: {
                js: /<script.*src=['"]([^'"]+)/gi,
                css: /<link.*href=['"]([^'"]+)/gi
              },
              replace: {
                js : function (filePath) {
                  return '[#if libraryName="'+getName(filePath)+'"]<script src="${baseUrl}/' + filePath + '"></script>[/#if]';  
                },
                css : function (filePath) {
                  return '[#if libraryName="'+getName(filePath)+'" ]<link rel="stylesheet" href="${baseUrl}/' + filePath + '" />[/#if]';
                }
              }
            }
          }
        },
        onMainNotFound: function(pkg) {
          console.log('name-of-bower-package-without-main: ' +pkg);
        },
      }
    }
  });
  
  grunt.registerTask('default', ['wiredep']);
};


function getName(filePath){
  return filePath.split('/')[1]
}