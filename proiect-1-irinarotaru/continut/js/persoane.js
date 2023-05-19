function incarcaPersoane()
{
   console.log("m-am apelat");
   if (window.XMLHttpRequest)
   {
      var req=new XMLHttpRequest(); 
      req.open("GET", "resurse/persoane.xml");
      req.setRequestHeader("Content-type","application/xml");
      req.onreadystatechange=function()
      {
         if(this.readyState == 4 && this.status == 200)
         {
            var doc=this.responseXML;
            var persoane = doc.getElementsByTagName("persoana");
            var html = "<table><tr><th>Nume</th><th>Prenume</th><th>Varsta</th><th>Strada</th><th>Numar</th><th>Localitate</th><th>Judet</th><th>Tara</th><th>Liceu</th><th>Facultate</th></tr>";
            for (var i = 0; i < persoane.length; i++) {
                  var nume = persoane[i].getElementsByTagName("nume")[0].childNodes[0].nodeValue;
                  var prenume = persoane[i].getElementsByTagName("prenume")[0].childNodes[0].nodeValue;
                  var varsta = persoane[i].getElementsByTagName("varsta")[0].childNodes[0].nodeValue;
                  var strada = persoane[i].getElementsByTagName("strada")[0].childNodes[0].nodeValue;
                  var numar = persoane[i].getElementsByTagName("numar")[0].childNodes[0].nodeValue;
                  var localitate = persoane[i].getElementsByTagName("localitate")[0].childNodes[0].nodeValue;
                  var judet = persoane[i].getElementsByTagName("judet")[0].childNodes[0].nodeValue;
                  var tara = persoane[i].getElementsByTagName("tara")[0].childNodes[0].nodeValue;
                  var liceu = persoane[i].getElementsByTagName("liceu")[0].childNodes[0].nodeValue;
                  var facultate = persoane[i].getElementsByTagName("facultate")[0].childNodes[0].nodeValue;
                  html += "<tr><td>" + nume + "</td><td>" + prenume + "</td><td>" + varsta + "</td><td>" 
                  + strada + "</td><td>" + numar + "</td><td>" + localitate + "</td><td>" + judet + "</td><td>" + tara + "</td><td>" + liceu + "</td><td>" + facultate + "</td></tr>";
         }
         html += "</table>";
         document.getElementById("incarcare").style.display="none";
         document.getElementById("tabel").innerHTML = html;
         }  
      };
      req.send();
   }
}