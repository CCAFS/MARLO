function addictionClass(){
    var element = document.getElementById("navBar");
    element.classList.toggle("responsible");
    var elementd = document.getElementById("icon-nav");
    if(elementd.textContent == 'close'){
    elementd.innerHTML = 'menu';
    }else{
    elementd.innerHTML = 'close';
    }
    var elements = document.getElementById("navbarSupportedContent");
    elements.classList.toggle("collapses");
    elements.classList.remove("navbar-collapse");
    elements.classList.remove("collapse");
    
}