from flask import Flask, render_template, jsonify, request, redirect
import cx_Oracle
from datetime import datetime

app = Flask(__name__)
with open(app.root_path + '\config.cfg', 'r') as f:
    app.config['ORACLE_URI'] = f.readline()

con = cx_Oracle.connect("bd076", "bd076", "bd-dc.cs.tuiasi.ro:1539/orcl")


# cursuri_dans begin code
@app.route('/')
@app.route('/cursuri_dans')
def curs():
    cursuri = []

    cur = con.cursor()
    cur.execute('select * from cursuri_dans')
    for result in cur:
        curs = {}
        curs['id_curs'] = result[0]
        curs['nume_curs'] = result[1]
        curs['pret'] = result[2]
        curs['dificultate'] = result[3]
        curs['maxim_cursanti'] = result[4]
        curs['id_antrenor'] =result[5]
        curs['id_stil']=result[6]
        curs['id_sedinta'] = result[7]

        cursuri.append(curs)
    cur.close()
    return render_template('cursuri_dans.html', cursuri=cursuri)


@app.route('/addCurs', methods=['GET', 'POST'])
def add_curs():
    error = None
    if request.method == 'POST':
        antrenor_id=0
        stil_id={}
        sedinta_id={}
        name = "'"+request.form['nume_antrenor']+"%'"
        name2="'" + request.form['nume_stil'] + "'"
        name3="'" + request.form['id_sedinta'] + "%'"
        print(name3)
        curs = 0
        cur = con.cursor()
        cur.execute('select id_antrenor from antrenori where nume_antrenor like' + name)
        print(name)
        for result in cur:
            antrenor_id = result[0]
        print(antrenor_id)
        cur.close()
        cur = con.cursor()
        cur.execute('select id_stil from stiluri where nume_stil=' + name2)
        for result in cur:
            stil_id= result[0]
        print(result[0])
        cur.close()
        cur = con.cursor()
        cur.execute('select id_sedinta from orar where inceput like' + name3)
        print("aici")
        for result in cur:
            sedinta_id = result[0]
            print(sedinta_id)
        cur.close()
        cur = con.cursor()
        cur.execute('select max(id_curs) from cursuri_dans')
        for result in cur:
            curs = result[0]
        cur.close()
        curs += 1
        cur = con.cursor()
        values = []
        values.append("'" + str(curs) + "'")

        values.append("'" + request.form['nume_curs'] + "'")
        values.append("'" + request.form['pret'] + "'")
        values.append("'" + request.form['dificultate'] + "'")
        maxim=request.form['maxim_cursanti']
        values.append("'"+maxim+"'")
        values.append("'" + str(antrenor_id) + "'")
        values.append("'" + str(stil_id) + "'")
        values.append("'" + request.form['id_sedinta'] + "'")
        cur.close()

        """"
        cur = con.cursor()
        cur.execute('select id_antrenor from cursuri_dans where id_curs=' + str(curs))
        for result in cur:
            antr = result[0]
            print(antr)
        cur.close()
        """
        maxant=0
        cur = con.cursor()
        cur.execute('select nr_maxim from antrenori where id_antrenor=' + str(antrenor_id))
        for result in cur:
            maxant = result[0]
        cur.close()
        cur = con.cursor()
        cur.execute('select count(*) from cursuri_dans where id_antrenor=' + str(antrenor_id))
        for result in cur:
            nrpredari = result[0]
        cur.close()
        cur = con.cursor()
        i = 0
        cur.execute('select id_curs from cursuri_dans where id_antrenor=' + str(antrenor_id))
        c = []
        while i < nrpredari:
            for result in cur:
                c.append(result[i])
            i = i + 1
        i = 0
        suma = 0
        while i < nrpredari:
            cur = con.cursor()
            cur.execute('select maxim_cursanti from cursuri_dans where id_curs=' + str(c[i]))
            for result in cur:
                suma += result[0]
            cur.close
            i=i+1

        if (suma+int(maxim))<=maxant:
            cur=con.cursor()
            fields = ['id_curs', 'nume_curs', 'pret', 'dificultate', 'maxim_cursanti','id_antrenor', 'id_stil','id_sedinta']
            query = 'INSERT INTO %s (%s) VALUES (%s)' % ('cursuri_dans', ', '.join(fields), ', '.join(values))

            cur.execute(query)
            cur.execute('commit')
            return redirect('/cursuri_dans')
        else:
            return redirect('/cursuri_dans')
    else:
        ant=[]
        sed=[]
        stil=[]
        cur = con.cursor()
        cur.execute('select nume_antrenor from antrenori')
        for result in cur:
            print(result[0])
            ant.append(result[0])
        cur.close()

        cur = con.cursor()
        cur.execute('select id_sedinta from orar')
        for result in cur:
            sed.append(result[0])
            print(result[0])
        cur.close()

        cur = con.cursor()
        cur.execute('select nume_stil from stiluri')
        for result in cur:
            stil.append(result[0])
        cur.close()

        return render_template('addCurs.html',antrenor=ant,stil=stil,sedinta=sed)


@app.route('/delCurs', methods=['POST'])
def del_curs():
    curs = request.form['id_curs']
    cur = con.cursor()
    cur.execute('delete from cursuri_dans where id_curs=' + curs)
    cur.execute('commit')
    return redirect('/cursuri_dans')

@app.route('/editCurs', methods=['POST'])
def edit_curs():
    curs = 0
    ant=0
    stil=0
    cur = con.cursor()
    curs="'"+request.form['id_curs']+"'"
    nume_curs="'"+request.form['nume_curs']+"'"
    pret = request.form['pret']
    dificultate = "'"+request.form['dificultate']+"'"
    maxim_cursanti=request.form['maxim_cursanti']
    nume_antrenor="'"+request.form['nume_antrenor']+"%'"
    nume_stil="'"+request.form['nume_stil']+"'"
    id_sedinta=request.form['id_sedinta']
    cur=con.cursor()
    cur.execute('select id_antrenor from antrenori where nume_antrenor like'+nume_antrenor)
    for result in cur:
        ant=result[0]
    cur.close
    cur = con.cursor()
    cur.execute('select id_stil from stiluri where nume_stil=' + nume_stil)
    for result in cur:
        stil = result[0]
    cur.close
    print(curs)
    maxant = 0
    cur = con.cursor()
    cur.execute('select nr_maxim from antrenori where id_antrenor=' + str(ant))
    for result in cur:
        maxant = result[0]
    cur.close()
    cur = con.cursor()
    cur.execute('select count(*) from cursuri_dans where id_antrenor=' + str(ant))
    for result in cur:
        nrpredari = result[0]
    cur.close()
    cur = con.cursor()
    i = 0
    cur.execute('select id_curs from cursuri_dans where id_antrenor=' + str(ant))
    c = []
    while i < nrpredari:
        for result in cur:
            c.append(result[i])
        i = i + 1
    i = 0
    suma = 0
    while i < nrpredari:
        cur = con.cursor()
        cur.execute('select maxim_cursanti from cursuri_dans where id_curs=' + str(c[i]))
        for result in cur:
            suma += result[0]
        cur.close
        i=i+1
    if (suma+int(maxim_cursanti))<=maxant:
        query = "UPDATE cursuri_dans SET nume_curs=%s, pret=%s, dificultate=%s, maxim_cursanti=%s,id_antrenor=%s, id_stil=%s, id_sedinta=%s where id_curs=%s" % (nume_curs,pret,dificultate,maxim_cursanti,ant,stil,id_sedinta,curs)
        cur.execute(query)
        cur.execute('commit')
        return redirect('/cursuri_dans')
    else:
        return redirect('/cursuri_dans')


@app.route('/getCurs', methods=['POST'])
def get_curs():
    curs = request.form['id_curs']
    cur = con.cursor()
    cur.execute('select * from cursuri_dans where id_curs=' + curs)

    curs2 = cur.fetchone()
    id_curs=curs[0]
    nume_curs = curs2[1]
    pret = curs2[2]
    dificultate = curs2[3]
    maxim_cursanti=curs2[4]
    id_antrenor = curs2[5]
    id_stil = curs2[6]
    id_sedinta = curs2[7]
    cur.close()
    antrenor_name=''
    cur=con.cursor()
    cur.execute('select nume_antrenor from antrenori where id_antrenor='+str(id_antrenor))
    for result in cur:
        antrenor_name=result[0]
    cur.close()
    stil_name=''
    cur = con.cursor()
    cur.execute('select nume_stil from stiluri where id_stil=' + str(id_stil))
    for result in cur:
        stil_name= result[0]
    cur.close()
    stil=[]
    ant=[]
    sed=[]
    cur=con.cursor()
    cur.execute('select nume_antrenor from antrenori')
    for result in cur:
        ant.append(result[0])
    cur.close()
    cur = con.cursor()
    cur.execute('select nume_stil from stiluri')
    for result in cur:
        stil.append(result[0])
    cur.close()
    cur = con.cursor()
    cur.execute('select id_sedinta from orar')
    for result in cur:
        sed.append(result[0])
    cur.close()
    print("aic")
    print(nume_curs)
    return render_template('editCurs.html', nume_curs=nume_curs,pret=pret,dificultate=dificultate,maxim_cursanti=maxim_cursanti,
                           antrenor=ant, stil=stil,sedinta=sed, nume_antrenor=antrenor_name,nume_stil=stil_name,id_sedinta=id_sedinta,id_curs=id_curs)


# cursanti start code
@app.route('/cursanti')
def cursanti():
    cursanti = []

    cur = con.cursor()
    cur.execute('select * from cursanti')
    for result in cur:
        cursant = {}
        cursant['id_cursant'] = result[0]
        cursant['nume_cursant'] = result[1]

        cursanti.append(cursant)
    cur.close()
    return render_template('cursanti.html', cursanti=cursanti)


@app.route('/addCursant', methods=['POST'])
def add_cursant():
    if request.method == 'POST':
        pers=0
        cur = con.cursor()
        cur.execute('select max(id_cursant) from cursanti')
        for result in cur:
            pers=result[0]
        cur.close()
        pers+=1
        cur=con.cursor()
        values = []
        values.append("'" + str(pers) + "'")
        values.append("'" + request.form['nume_cursant'] + "'")
        fields = ['id_cursant','nume_cursant']
        query = 'INSERT INTO %s (%s) VALUES (%s)' % (
            'cursanti',
            ', '.join(fields),
            ', '.join(values)
        )

        cur.execute(query)
        cur.execute('commit')
        return redirect('/cursanti')

@app.route('/delCursant', methods=['POST'])
def del_cursant():
    cursant = request.form['id_cursant']
    cur = con.cursor()
    cur.execute('delete from cursanti where id_cursant=' + cursant)
    cur.execute('commit')
    return redirect('/cursanti')


@app.route('/getCursant',methods=['POST'])
def get_cursant():
    cursant=request.form['id_cursant']
    cur=con.cursor()
    cur.execute('select * from cursanti where id_cursant='+cursant)
    c=cur.fetchone()
    id_cursant=c[0]
    nume_cursant=c[1]
    print(id_cursant)
    print("get")
    cur.close()

    return render_template('editCursant.html',nume_cursant=nume_cursant,id_cursant=id_cursant)


@app.route('/editCursant',methods=['POST'])
def edit_cursant():
    nume_cursant="'"+request.form['nume_cursant']+"'"
    c=0
    print(nume_cursant)
    id_cursant="'"+request.form['id_cursant']+"'"
    print(id_cursant)
    cur=con.cursor()
    query="UPDATE cursanti SET nume_cursant=%s where id_cursant=%s" % (nume_cursant,id_cursant)
    cur.execute(query)
    cur.execute('commit')
    return redirect('/cursanti')


# cursanti end code
# ------------------------------------------#
# antrenori start code

@app.route('/antrenori')
def ant():
    antrenori = []

    cur = con.cursor()
    cur.execute('select * from antrenori')
    for result in cur:
        antrenor = {}
        antrenor['id_antrenor'] = result[0]
        antrenor['nr_maxim']=result[1]
        antrenor['nume_antrenor'] = result[2]

        antrenori.append(antrenor)
    cur.close()
    return render_template('antrenori.html', antrenori=antrenori)


@app.route('/addAntrenor', methods=['GET', 'POST'])
def ad_ant():
    error = None
    if request.method == 'POST':
        ant = 0
        cur = con.cursor()
        cur.execute('select max(id_antrenor) from antrenori')
        for result in cur:
            ant = result[0]
        cur.close()
        ant += 1
        cur = con.cursor()
        values = []
        values.append("'" + str(ant) + "'")
        values.append("'" + request.form['nr_maxim'] + "'")
        values.append("'" + request.form['nume_antrenor'] + "'")
        fields = ['id_antrenor','nr_maxim','nume_antrenor']
        query = 'INSERT INTO %s (%s) VALUES (%s)' % (
            'antrenori',
            ', '.join(fields),
            ', '.join(values)
        )

        cur.execute(query)
        cur.execute('commit')
        return redirect('/antrenori')


@app.route('/delAntrenor', methods=['POST'])
def del_ant():
    ant = request.form['id_antrenor']
    cur = con.cursor()
    cur.execute('delete from antrenori where id_antrenor=' + ant)
    cur.execute('commit')
    return redirect('/antrenori')


@app.route('/editAntrenor',methods=['POST'])
def edit_ant():
    nume_antrenor="'"+request.form['nume_antrenor']+"'"
    nr_maxim=request.form['nr_maxim']
    id_antrenor="'"+request.form['id_antrenor']+"'"
    cur = con.cursor()
    print(nume_antrenor)
    cur.execute('select id_antrenor from antrenori where nume_antrenor=' + nume_antrenor)
    for result in cur:
        ant = result[0]
    cur.close
    cur = con.cursor()
    nrpredari=0
    print(ant)
    print(id_antrenor)
    print(type(ant))
    print(type(id_antrenor))
    cur.execute('select count(*) from cursuri_dans where id_antrenor=' + str(ant))
    for result in cur:
        nrpredari = result[0]
        print(nrpredari)
    cur.close()
    cur = con.cursor()
    i = 0
    cur.execute('select id_curs from cursuri_dans where id_antrenor=' + str(ant))
    c = []
    while i < nrpredari:
        for result in cur:
            c.append(result[i])
            print('id uri')
            print(c)
        i = i + 1
    cur.close()
    i = 0
    suma = 0
    while i < nrpredari:
        cur = con.cursor()
        cur.execute('select maxim_cursanti from cursuri_dans where id_curs=' + str(c[i]))
        for result in cur:
            suma += result[0]
            print("suma")
            print(suma)
        cur.close
        i=i+1
    print(suma)
    print(nr_maxim)
    if suma <= int(nr_maxim):
        cur=con.cursor()
        query = "UPDATE antrenori SET nume_antrenor=%s, nr_maxim=%s where id_antrenor=%s" % (
        nume_antrenor, nr_maxim, id_antrenor)
        cur.execute(query)
        cur.execute('commit')
        return redirect('/antrenori')
    else:
        return redirect('/antrenori')


@app.route('/getAntrenor',methods=['POST'])
def get_antrenor():
    ant=request.form['id_antrenor']
    cur=con.cursor()
    cur.execute('select * from antrenori where id_antrenor='+ant)
    c=cur.fetchone()
    id_antrenor=c[0]
    nr_maxim=c[1]
    nume_antrenor=c[2]
    cur.close()
    return render_template('editAntrenor.html',id_antrenor=id_antrenor,nr_maxim=nr_maxim,nume_antrenor=nume_antrenor)


@app.route('/stiluri')
def stil():
    stiluri = []

    cur = con.cursor()
    cur.execute('select * from stiluri')
    for result in cur:
        stil = {}
        stil['id_stil'] = result[0]
        stil['nume_stil'] = result[1]

        stiluri.append(stil)
    cur.close()
    return render_template('stiluri.html', stiluri=stiluri)


@app.route('/addStil', methods=['POST'])
def add_stil():
    error = None
    if request.method == 'POST':
        stil = 0
        cur = con.cursor()
        cur.execute('select max(id_stil) from stiluri')
        for result in cur:
            stil = result[0]
        cur.close()
        stil += 1
        cur = con.cursor()
        values = []
        values.append("'" + str(stil) + "'")
        values.append("'" + request.form['nume_stil'] + "'")
        fields = ['id_stil','nume_stil']
        query = 'INSERT INTO %s (%s) VALUES (%s)' % (
            'stiluri',
            ', '.join(fields),
            ', '.join(values)
        )

        cur.execute(query)
        cur.execute('commit')
        return redirect('/stiluri')


@app.route('/delStil', methods=['POST'])
def del_stil():
    stil = request.form['id_stil']
    cur = con.cursor()
    cur.execute('delete from stiluri where id_stil=' + stil)
    cur.execute('commit')
    return redirect('/stiluri')

@app.route('/getStil',methods=['POST'])
def get_stil():
    stil=request.form['id_stil']
    cur=con.cursor()
    cur.execute('select * from stiluri where id_stil='+stil)
    c=cur.fetchone()
    id_stil=c[0]
    nume_stil=c[1]
    cur.close()

    return render_template('editStil.html',nume_stil=nume_stil,id_stil=id_stil)


@app.route('/editStil',methods=['POST'])
def edit_stil():
    nume_stil="'"+request.form['nume_stil']+"'"
    c=0
    id_stil="'"+request.form['id_stil']+"'"
    cur=con.cursor()
    query="UPDATE stiluri SET nume_stil=%s where id_stil=%s" % (nume_stil,id_stil)
    cur.execute(query)
    cur.execute('commit')
    return redirect('/stiluri')


# stiluri end code
# -------------------------------------------#
# orar start code
@app.route('/orar')
def orar():
    orar = []

    cur = con.cursor()
    cur.execute('select * from orar')
    for result in cur:
        sedinta = {}
        sedinta['id_sedinta'] = result[0]
        sedinta['inceput'] = result[1]
        sedinta['final'] = result[2]

        orar.append(sedinta)
    cur.close()
    return render_template('orar.html', orar=orar)


@app.route('/addSedinta', methods=['POST'])
def add_sedinta():
    error = None
    if request.method == 'POST':
        ora = 0
        cur = con.cursor()
        cur.execute('select max(id_sedinta) from orar')
        for result in cur:
            ora = result[0]
        cur.close()
        ora += 1
        cur = con.cursor()
        values = []
        values.append(str(ora))
        values.append(
            "to_timestamp(" + "substr(" + "'" + request.form['inceput'] + "', 1, 10)" + "||" + "substr(" + "'" +
            request.form['inceput'] + "', 12, 8)" + ", " + "'YYYY-MM-DD HH24:MI:SS')")
        values.append(
            "to_timestamp(" + "substr(" + "'" + request.form['final'] + "', 1, 10)" + "||" + "substr(" + "'" +
            request.form['final'] + "', 12, 8)" + ", " + "'YYYY-MM-DD HH24:MI:SS')")
        fields = ['id_sedinta', 'inceput', 'final']
        query = 'INSERT INTO %s (%s) VALUES (%s)' % ('orar', ', '.join(fields), ', '.join(values))
        cur.execute(query)
        cur.execute('commit')
        return redirect('/orar')


@app.route('/delSedinta', methods=['POST'])
def del_orar():
    sedinta = request.form['id_sedinta']
    cur = con.cursor()
    cur.execute('delete from orar where id_sedinta=' + sedinta)
    cur.execute('commit')
    return redirect('/orar')

# orar end code

#inscrieri start code
@app.route('/inscrieri')
def inscrieri():
    inscrieri = []

    cur = con.cursor()
    cur.execute('select * from inscrieri')
    for result in cur:
        ins = {}
        ins['Cursuri_Dans_id_curs'] = result[0]
        ins['Cursanti_id_cursant'] = result[1]

        inscrieri.append(ins)
    cur.close()
    return render_template('inscrieri.html', inscrieri=inscrieri)

@app.route('/addInscriere', methods=['POST'])
def add_inscriere():
    error = None
    if request.method == 'POST':
        curs=request.form['Cursuri_Dans_id_curs']
        cur = con.cursor()
        cur.execute('select maxim_cursanti from cursuri_dans where id_curs='+curs)
        for result in cur:
            maxim = result[0]
        cur.close()
        cur = con.cursor()
        cur.execute('select count(*) from inscrieri where Cursuri_Dans_id_curs=' + curs)
        for result in cur:
            actual = result[0]
        cur.close()
        if actual<maxim:
            cur = con.cursor()
            values = []
            values.append(curs)
            values.append("'" + request.form['Cursanti_id_cursant'] + "'")
            fields = ['Cursuri_Dans_id_curs','Cursanti_id_cursant']
            query = 'INSERT INTO %s (%s) VALUES (%s)' % (
                'inscrieri',
                ', '.join(fields),
                ', '.join(values)
            )
            cur.execute(query)
            cur.execute('commit')
            return redirect('/inscrieri')
        else:
            return redirect('/inscrieri')


@app.route('/delInscriere', methods=['POST'])
def del_inscriere():
    ins = request.form['Cursanti_id_cursant']
    cur = con.cursor()
    cur.execute('delete from inscrieri where Cursanti_id_cursant=' + ins)
    cur.execute('commit')
    return redirect('/inscrieri')
#inscrieri end code

# main
if __name__ == '__main__':
    app.run(debug=True)
    con.close()
