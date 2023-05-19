function adresaURL(){
    let elem=document.getElementById("timpCurent");
    if(elem!=null)
    {
        elem.innerHTML="Timpul curent este "+(new Date());
    }
    let elem2=document.getElementById("adresaMea");
    if(elem2!=null)
        elem2.innerHTML=window.location.href;
    navigator.geolocation.getCurrentPosition(getlocation);
    let elem3=document.getElementById("browserVer");
    if(elem3!=null)
        elem3.innerHTML="Versiunea browser-ului este "+navigator.appVersion;
    let elem4=document.getElementById("browserName");
    if(elem4!=null)
        elem4.innerHTML="Numele browser-ului este "+navigator.appName;
}

function getlocation(position)
{
    let elem=document.getElementById("locatiaMea");
    if(elem!=null)
        elem.innerHTML="Locatia curenta este la latitudine "+position.coords.latitude+" si la longitudine "+position.coords.longitude;
}

var x1=-1,y1=-1;
function deseneaza(e)
{
    let x=e.offsetX;
    let y=e.offsetY;
    if(x1==-1)
    {
        x1=x;
        y1=y;
    }
    else{
        if(x1>x)
        {
            let aux=x1;
            x1=x;
            x=aux;
        }
        if(y1>y)
        {
            let aux=y1;
            y1=y;
            y=aux;
        }
        var c=document.getElementById("desen");
        var ctx=c.getContext("2d");
        let col=document.getElementById("favcolor").value;
        let col2=document.getElementById("favcolor2").value;
        ctx.strokeStyle=col2;
        ctx.fillStyle=col;
        ctx.fillRect(x1,y1,Math.abs(x-x1),Math.abs(y-y1));
        ctx.strokeRect(x1,y1,Math.abs(x-x1),Math.abs(y-y1));
        x1=y1=-1;
    }

}

function schimbaContinut(resursa,fisier,functie)
{
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange =
        function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                document.getElementById("continut").innerHTML = xhttp.responseText;
                if (fisier) {
                    var elementScript = document.createElement('script');
                    elementScript.onload = function () {
                        console.log("hello");
                        if (functie) {
                            window[functie]();
                        }
                    };
                    elementScript.src = fisier;
                    document.head.appendChild(elementScript);
                } else {
                    if (functie) {
                        window[functie]();
                    }
                }
                //let myJson = JSON.parse(xhttp.responseText);
            }
    }
    xhttp.open("GET", resursa+".html", true);
    xhttp.send();
    }
}

function tabel()
{
    var tab = document.getElementById("t");
    var poz=document.getElementById("index").value;
    var linie=tab.insertRow(poz);
    var col=document.getElementById("favcolor3").value;
    for(var i=0;i<tab.rows[0].cells.length;i++)
    {
        var coloana=linie.insertCell(i);
        coloana.style.backgroundColor=col;
    }
}
function tabel2()
{
    var tab = document.getElementById("t");
    var poz=document.getElementById("index").value;
    var col=document.getElementById("favcolor3").value;
    for(var i=0;i<tab.rows.length;i++)
    {
        var coloana=tab.rows[i].insertCell(poz);
        coloana.style.backgroundColor=col;
    }
}

function verifica()
{
    console.log("aici ver");
    var utilizator=document.getElementById("nume").value;
    var parola=document.getElementById("pass").value;
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange =
        function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                var utilizatori=JSON.parse(xhttp.responseText);
                var cauta=false;
                for(var i=0;i<utilizatori.length;i++)
                {
                    if(utilizatori[i].utilizator==utilizator && utilizatori[i].parola==parola)
                    {
                        cauta=true;
                        break;
                    }
                }
                var verdict=document.getElementById("ver");
                if(cauta)
                {
                    verdict.innerHTML="S-au gasit datele";
                }
                else
                {
                    verdict.innerHTML="Nu s-au gasit datele";
                }
            }
    }
    xhttp.open("GET", "resurse/utilizatori.json", true);
    xhttp.send();
    }
}

function cerere()
{
    console.log("aici cerere");
    var utilizator=document.getElementById("name").value;
    var parola=document.getElementById("pwd").value;
    var xhttp=new XMLHttpRequest();
    var obj=new Object();
    obj.utilizator=utilizator;
    obj.parola=parola;
    var json=JSON.stringify(obj);
    xhttp.open("POST","/api/utilizatori",true);
    xhttp.send(json);
}