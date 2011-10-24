/*
jQuery(function(){
  laquu("#content ul").tab();
});
*/

/* TAB Menu */
$(function () {
  var mainContainer = $('#main-container .main-content');
  mainContainer.hide().filter(':first').show();
  $('#tab-navigation a').click(function() {
    mainContainer.hide();
    mainContainer.filter(this.hash).show();
    $('#tab-navigation a').removeClass('active');
    $(this).addClass('active');
    return false;
  });
});

