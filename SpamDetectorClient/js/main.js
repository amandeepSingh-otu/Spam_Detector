// TODO: onload function should retrieve the data needed to populate the UI
let divBody=document.getElementById("body-sec").innerHTML;
let accuracyOutput;
let precisionOutput;
let accuracy;
let precision;
let tableBody;
let tableData ;
function loadAbout() {
  document.getElementById("body-sec").innerHTML='<object type="text/html" data="about.html" ' +
    'width="100%" height="100%"></object>';

}
function loadBody(){
  document.getElementById("body-sec").innerHTML = divBody;
  document.getElementById("accuracy").innerHTML = accuracyOutput.value;
  document.getElementById("precision").innerHTML = precisionOutput.value;
  drawChart(accuracy,"accuracyPie");
  drawChart(precision,"precisionPie");
  populateSpamTable(tableData);

}
function requestAccuracy(){
  let apiUrl = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy" ;
  fetch(apiUrl, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }

  }).then(response => response.json())
    .then(response=> loadAccuracy(response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });
}
function requestPrecision(){
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
  precision = (response.value*100);
  drawChart(precision,"precisionPie");

  precisionOutput.value = precision.toFixed(2) +"%";

}

function loadAccuracy(response){

  accuracyOutput = document.getElementById('accuracy');
  accuracy = (response.value*100);
  drawChart(accuracy,"accuracyPie");

  accuracyOutput.value = accuracy.toFixed(2) + "%";
}

function fetchSpamData() {
  let apiUrl = "http://localhost:8080/spamDetector-1.0/api/spam/";
  fetch(apiUrl, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }
  })
    .then(response => response.json())
    .then(data => populateSpamTable(data))
    .catch(error => console.error("Failed to fetch spam data: ", error));
}

function populateSpamTable(data) {
  tableData = data;

  tableBody = document.getElementById("table-body-ref");
  tableBody.innerHTML = ""; // Clear existing table data if any

  data.forEach(item => {
    const row = document.createElement("tr");

    const fileCell = document.createElement("td");
    fileCell.textContent = item.filename;
    row.appendChild(fileCell);

    const spamProbCell = document.createElement("td");
    spamProbCell.textContent = item.spamProbRounded.toFixed(2);
    row.appendChild(spamProbCell);

    const classCell = document.createElement("td");
    classCell.textContent = item.actualClass;
    row.appendChild(classCell);

    tableBody.appendChild(row);
  });
}


(function () {
 // requestDataFromServer(apiUrl);
  requestAccuracy();
  requestPrecision();
  fetchSpamData();

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
