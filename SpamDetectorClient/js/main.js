// TODO: onload function should retrieve the data needed to populate the UI
let divbody=document.getElementById("body-sec").innerHTML;
let acuarcyOuput;
let precisionOutput;
function loadAbout() {
  document.getElementById("body-sec").innerHTML='<object type="text/html" data="about.html" ' +
    'width="100%" height="100%"></object>';

}
function loadBody(){
  document.getElementById("body-sec").innerHTML = divbody;
  document.getElementById("accuracy").innerHTML = acuarcyOuput.value;
  document.getElementById("precision").innerHTML = precisionOutput.value;

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
  let precision = response.accuracy*100;
  precisionOutput.value = precision

  drawChart(precision,"precisionPie")

}

function loadAcuarcy(response){

  acuarcyOuput = document.getElementById('accuracy');
  accuarcy = response.precision*100;
  acuarcyOuput.value = accuarcy;
 // document.getElementById('accuracyBar').style.width = `${accuarcy}%`;
  drawChart(accuarcy,"accuracyPie");


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

  // Define the data to be used in the pie chart
  let data = google.visualization.arrayToDataTable([
    ['Category', 'Percentage'],
    ['Category 1', value],
    ['Category 2', 100-value],
  ]);

  // Set options for the pie chart
  let options = {

    legend: 'none', // Set legend to 'none' to hide it
  };

  // Create a new pie chart and attach it to the container
  let chart = new google.visualization.PieChart(document.getElementById(id));

  // Draw the chart with the defined data and options
  chart.draw(data, options);
}
