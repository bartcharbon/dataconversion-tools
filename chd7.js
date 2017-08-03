function count(url, id){
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
      var status = xhr.status;
      if (status == 200) {
        setResult(xhr.response.total, id);
      } 
    };
    xhr.send();
    }

function setResult(result, id)  {
    document.getElementById(id).innerHTML = result;
}

count("url?q=*=q=query&attrs=~id&num=0","div id")
