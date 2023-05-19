onmessage=function(e)
{
    console.log("Am primit mesajul "+e.data);
    console.log("Urmeaza sa se trimita raspuns");
    postMessage("actualizeaza");
}