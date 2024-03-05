// TODO: onload function should retrieve the data needed to populate the UI
let divbody=document.getElementById("body-sec").innerHTML;
let acuarcyOuput;
let precisionOutput;
let accuarcy;
let precision;
function loadAbout() {
  document.getElementById("body-sec").innerHTML='<object type="text/html" data="about.html" ' +
    'width="100%" height="100%"></object>';

}
function loadBody(){
  document.getElementById("body-sec").innerHTML = divbody;
  document.getElementById("accuracy").innerHTML = acuarcyOuput.value;
  document.getElementById("precision").innerHTML = precisionOutput.value;
  drawChart(accuarcy,"accuracyPie");
  drawChart(precision,"precisionPie");


}
function requestAccuarcy(){
  let apiUrl = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy" ;
  fetch(apiUrl, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }

  }).then(response => response.json())
    .then(response=> loadAcuarcy(response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });



}
function requestPreciosn(){
  let apiUrl = "http://localhost:8080/spamDetector-1.0/api/spam/precision" ;
  fetch(apiUrl, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }

  }).then(response => response.json())
    .then(response=> loadPrecision(response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });

}
function loadPrecision(response){
  precisionOutput = document.getElementById("precision");
  precision = (response.accuracy*100);


  drawChart(precision,"precisionPie");

  precisionOutput.value = precision.toFixed(2) +"%";


}

function loadAcuarcy(response){

  acuarcyOuput = document.getElementById('accuracy');
  accuarcy = (response.precision*100);

 // document.getElementById('accuracyBar').style.width = `${accuarcy}%`;
  drawChart(accuarcy,"accuracyPie");

  acuarcyOuput.value = accuarcy.toFixed(2) + "%";


}
(function () {
 // requestDataFromServer(apiUrl);
  requestAccuarcy();
  requestPreciosn();

})();
google.charts.load('current', {'packages':['corechart']});

// Set a callback to run when the Google Charts library is loaded
google.charts.setOnLoadCallback(drawChart);
function drawChart(value,id) {

  let data = google.visualization.arrayToDataTable([
    ['Category', 'Percentage'],
    ['Category 1', value],
    ['Category 2', 100-value],
  ]);

  let options = {

    legend: 'none', // Set legend to 'none' to hide it
  };


  let chart = new google.visualization.PieChart(document.getElementById(id));

  chart.draw(data, options);
}
