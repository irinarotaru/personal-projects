class Produs{
    constructor(nume,cantitate)
    {
        this.nume=nume;
        this.cantitate=cantitate;
    }
}

class Produs2{
    constructor(id,nume,cantitate)
    {
        this.id=id;
        this.nume=nume;
        this.cantitate=cantitate;
    }
}

class myStorageInterface{
    constructor(nume,cantitate)
    {
        this.produs=new Produs(nume,cantitate);
    }

    adaugaProdus(){}
}

class LocalStorage extends myStorageInterface{
    constructor(nume, cantitate){
        super(nume, cantitate);
    }

    adaugaProdus(){
        let n = localStorage.getItem("nr");
        if(n == null)
            n = 0;
        else
            n = parseInt(n);
    
        let prod = new Produs(this.produs.nume, this.produs.cantitate);
        n++;
        localStorage.setItem("nr", n);
        localStorage.setItem(n, JSON.stringify(prod));
    }

    adaugaLinie() {
        let n = localStorage.getItem("nr");
        if(n == null)
            n = 0;
        else
            n = parseInt(n);

        var table = document.getElementById("listacump");
        var row = table.insertRow(n);
    
        var nrCell = row.insertCell(0);
        var nameCell = row.insertCell(1);
        var quanCell = row.insertCell(2);
    
        var jsonString = localStorage.getItem(n);
        var jsonObject = JSON.parse(jsonString);
        nrCell.innerHTML = n;
        nameCell.innerHTML = jsonObject.nume;
        quanCell.innerHTML = jsonObject.cantitate;
        console.log("am adaugat linie");
    }
}

var n_db = 0;
class IndexDB extends myStorageInterface{
    constructor(nume, cantitate){
        super(nume, cantitate);
    }
    adaugaProdus(){
        var numeProdus = this.produs.nume;
        var cantitateProdus =  this.produs.cantitate;
        var openRequest = window.indexedDB.open("BazaDeDateProduse", 1);
        openRequest.onerror = event =>{
            console.log("Eroare conectare la baza de date");
        }

        openRequest.onupgradeneeded = event=>{
            var db = event.target.result;
            if(db.objectStoreNames.contains("produse") == 0)
                var objectStore = db.createObjectStore("produse", { keyPath: "id" });

            objectStore.createIndex("id", "id", {unique:false});
            objectStore.createIndex('nume', 'nume', {unique:false});
            objectStore.createIndex('cantitate', 'cantitate', {unique:false});
        }

        openRequest.onsuccess = function(event){
            var db = openRequest.result;
            var transaction = db.transaction("produse", "readwrite");
            var produse = transaction.objectStore("produse");

            var allIds = produse.index("id");
            var allIdsRequest = allIds.getAll();
            allIdsRequest.onsuccess = function(){
                n_db = allIdsRequest.result.length;
                n_db++;
                var newProduct = new Produs2(n_db, numeProdus,cantitateProdus);
                var request = produse.put({
                    id: newProduct.id,
                    nume : newProduct.nume,
                    cantitate : newProduct.cantitate
                });
                request.onsuccess = function(){
                    console.log("se adauga produs nou: " + newProduct.nume + " " + newProduct.cantitate);
                    var table = document.getElementById("listacump");
                    table.innerHTML = `<tr>
                    <th> Nr </th>
                    <th> Nume Produs</th>
                    <th> Cantitate </th>
                </tr>`;
                    var allIds = produse.index("id");
                    var allIdsRequest = allIds.getAll();
                    allIdsRequest.onsuccess = function(){
                        console.log("am facut rost de toate id-urile");
                        for (var i = 1; i <= allIdsRequest.result.length; i++) {
                            var row = table.insertRow(allIdsRequest.result[i - 1].id);
                            var cell0 = row.insertCell(0);
                            var cell1 = row.insertCell(1);
                            var cell2 = row.insertCell(2);
                            cell0.innerHTML = allIdsRequest.result[i - 1].id;
                            cell1.innerHTML = allIdsRequest.result[i - 1].nume;
                            cell2.innerHTML = allIdsRequest.result[i - 1].cantitate;
                          }
                    }                
                }
                request.onerror = function()
                {
                  console.log("eroare la adaugare produs.");
                }
            }
        }
    }
}

function adauga()
{
    incarcaTabel();
    console.log("in functia adauga");
    var worker=new Worker("js/worker.js");
    worker.postMessage("adauga");
    worker.onmessage=function(event)
    {
        let response=event.data;
        if(response=="actualizeaza")
        {
            console.log("se adauga in tabel");
        }
    };
    var local=document.getElementById("local").checked;
    var db=document.getElementById("db").checked;
    var nume=document.getElementById("name").value;
    var cant=document.getElementById("quan").value;
    if(local)
    {
        console.log("am ajuns pe local");
        let localStorage=new LocalStorage(nume,cant);
        localStorage.adaugaProdus();
        localStorage.adaugaLinie();
    }
    if(db)
    {
        let indexDB=new IndexDB(nume,cant);
        indexDB.adaugaProdus();
    }
}

function incarcaTabel(){
    let n = localStorage.getItem("nr");
    if(n == null)
        n = 0;
    else
        n = parseInt(n);
    
    var table = document.getElementById("listacump");
    for(let i = 1; i <= n; i++)
    {
        console.log("am inserat in tabel randul " + i);

        let jsonString = localStorage.getItem(i);
        let jsonObject = JSON.parse(jsonString);

        let row = table.insertRow(i);
        let idCell = row.insertCell(0);
        idCell.innerHTML = i;
        let nameCell = row.insertCell(1);
        nameCell.innerHTML = jsonObject.nume;
        let quantityCell = row.insertCell(2);
        quantityCell.innerHTML = jsonObject.cantitate;
    }   
}