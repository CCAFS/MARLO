module.exports = function(grunt) {

  grunt.loadNpmTasks('grunt-wiredep');
  grunt.loadNpmTasks('grunt-jsdoc');
  
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
    },
    jsdoc : {
      dist : {
          src: ['src/main/webapp/js/global/*.js', 
                'src/main/webapp/js/admin/*.js',
                'src/main/webapp/js/fundingSources/*.js',
                'src/main/webapp/js/home/*.js',
                'src/main/webapp/js/impactPathway/*.js',
                'src/main/webapp/js/projects/*.js',
                'src/main/webapp/js/publications/*.js',
                'src/main/webapp/js/summaries/*.js',
                'src/main/webapp/js/superadmin/*.js',
                'src/main/webapp/js/synthesis/*.js',
                'src/main/webapp/js/user/*.js',
               ],
          options: {
              destination: 'src/main/webapp/jsdoc'
          }
      }
  }
  });
  
  grunt.registerTask('default', ['wiredep']);
};


function getName(filePath){
  return filePath.split('/')[1]
}