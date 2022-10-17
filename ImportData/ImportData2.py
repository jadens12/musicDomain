import psycopg2
from sshtunnel import SSHTunnelForwarder
from mysql.connector import Error
import random


def execute_query(connection, query):
    #print( query )
    cursor = connection.cursor()
    try:
        cursor.execute(query)
        connection.commit()
    except Error as err:
        print(f"Error: '{err}'")


def insert_genre_song( conn, sid, genre ):
    query = "INSERT INTO genre_song VALUES( " + str(sid) + ", '" + genre + "')"
    execute_query( conn, query )

def insert_genre_album( conn, aid, genre ):
    query = "INSERT INTO genre_album VALUES( " + str(aid) + ", '" + genre + "')"
    execute_query( conn, query )



username = "USERNAME"
password = "PASSWORD"
dbName = "p32001_18"

try:
    with SSHTunnelForwarder(('starbug.cs.rit.edu', 22),
                            ssh_username=username,
                            ssh_password=password,
                            remote_bind_address=('localhost', 5432)) as server:
        server.start()
        print("SSH tunnel established")
        params = {
            'database': dbName,
            'user': username,
            'password': password,
            'host': 'localhost',
            'port': server.local_bind_port
        }

        conn = psycopg2.connect(**params)
        curs = conn.cursor()
        print("Database connection established")

        print("Deleting rows from tables")
        query = "DELETE FROM album_artist"
        execute_query(conn, query)
        query = "DELETE FROM album_song"
        execute_query(conn, query)
        query = "DELETE FROM song_artist"
        execute_query(conn, query)
        query = "DELETE FROM genre_album"
        execute_query(conn, query)
        query = "DELETE FROM genre_song"
        execute_query(conn, query)
        query = "DELETE FROM song"
        execute_query(conn, query)
        query = "DELETE FROM artist"
        execute_query(conn, query)
        query = "DELETE FROM album"
        execute_query(conn, query)
        query = "DELETE FROM genre"
        execute_query(conn, query)
        print("Finished deleting rows from tables")

        print("Inserting genres")
        genres = ["pop", "rock", "indie", "folk", "hip hop", "country", "edm", "rap", "metal"]
        for genre in genres:
            query = "INSERT INTO genre VALUES( '" + genre + "')"
            execute_query( conn, query )
        print("Finished inserting genres")

        file = open("ImportData/moreData.txt", "r")

        sid = 1
        aid = 0
        trackNum = 1

        currentArtist = ""
        currentAlbum = ""
        currentDate = ""
        albumGenres = []
        albumNumTracks = 0

        print("Reading file")
        for line in file:
            groups = line.split(",")
            year = groups[0].strip()
            song = groups[1].strip()
            artist = groups[2].strip()

            if len(groups) == 3 and len(song) < 50 and len(artist) < 50 and "'" not in song and "'" not in artist:
                if artist != currentArtist:
                    currentArtist = artist
                    query = "INSERT INTO artist VALUES( '" + artist + "')"
                    execute_query( conn, query )
                    if trackNum <= albumNumTracks and random.random() < .3:
                        query = "INSERT INTO album_artist VALUES( " + str(aid) + ", '" + currentArtist + "')"
                        execute_query( conn, query )
                    else:
                        albumNumTracks = 0

                if trackNum > albumNumTracks:
                    currentAlbum = song
                    albumGenres.clear
                    albumNumTracks = random.randrange( 1, 12 )
                    aid += 1

                    randMon = int(random.randrange(1, 12))
                    randDay = int(random.randrange(1, 28))
                    currentDate = "" + str(year) + "-" + str(randMon) + "-" + str(randDay)

                    query = "INSERT INTO album VALUES( " + str(aid) + ", '" + str(currentDate) + "', '" + song + "')"
                    execute_query( conn, query )

                    query = "INSERT INTO album_artist VALUES( " + str(aid) + ", '" + currentArtist + "')"
                    execute_query( conn, query )

                    genre_sample = random.sample( genres, 3 )
                    insert_genre_album( conn, aid, genre_sample[0] )
                    albumGenres.append( genre_sample[0] )
                    if random.random() < .8:
                        insert_genre_album( conn, aid, genre_sample[1] )
                        albumGenres.append( genre_sample[1] )
                        if random.random() < .8:
                            insert_genre_album( conn, aid, genre_sample[2] )
                            albumGenres.append( genre_sample[2] )

                    trackNum = 1

                randLength = random.randrange(100,500)
                query = "INSERT INTO song VALUES( " + str(sid) + ", '" + song + "', '" + str(currentDate) + "', " + str(randLength) + ")"
                execute_query( conn, query )

                query = "INSERT INTO album_song VALUES( " + str(sid) + ", " + str(aid) + ", " + str(trackNum) + ")"
                execute_query( conn, query )

                query = "INSERT INTO song_artist VALUES( " + str(sid) + ", '" + currentArtist + "')"
                execute_query( conn, query )

                random.shuffle( albumGenres )
                insert_genre_song( conn, sid, genre_sample[0] )
                if random.random() < .3 and len(albumGenres) > 1:
                    insert_genre_song( conn, sid, genre_sample[1] )
                    if random.random() < .3 and len(albumGenres) > 2:
                        insert_genre_song( conn, sid, genre_sample[2] )

                sid += 1
                trackNum += 1

        print("Finished reading file")

except:
    print("Connection failed")
finally:
    file.close()
    conn.close()
