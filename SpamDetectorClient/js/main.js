// TODO: onload function should retrieve the data needed to populate the UI
let divbody = document.getElementById("body-sec").innerHTML;
function loadAbout() {
  document.getElementById("body-sec").innerHTML='<object type="text/html" data="about.html" ' +
    'width="100%" height="100%"></object>';



}
function loadBody(){
  document.getElementById("body-sec").innerHTML = divbody;

}



