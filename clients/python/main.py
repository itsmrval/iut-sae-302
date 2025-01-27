import tkinter as tk
from tkinter import messagebox, ttk, simpledialog 
import socket
import ssl
from datetime import datetime
import time
from tkcalendar import Calendar

class SSLSocket:
    def __init__(self):
        # Create SSL context
        self.context = ssl.create_default_context()
        # Don't verify server certificate (for development/testing)
        self.context.check_hostname = False
        self.context.verify_mode = ssl.CERT_NONE
        
        # Create socket and wrap with SSL
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.ssl_sock = None
    
    def connect(self, host, port):
        try:
            self.sock.connect((host, port))
            self.ssl_sock = self.context.wrap_socket(self.sock, server_hostname=host)
            return True
        except Exception as e:
            messagebox.showerror("Connection Error", f"Failed to establish SSL connection: {str(e)}")
            return False
    
    def send(self, data):
        if self.ssl_sock:
            return self.ssl_sock.send(data.encode())
        return 0
    
    def recv(self, buffer_size):
        if self.ssl_sock:
            return self.ssl_sock.recv(buffer_size)
        return b''
    
    def close(self):
        if self.ssl_sock:
            self.ssl_sock.close()
        self.sock.close()


class TimeSelector(tk.Frame):
    def __init__(self, master):
        super().__init__(master)

        # Calendar for date selection
        self.calendar = Calendar(self, date_pattern='yyyy-mm-dd')
        self.calendar.pack(fill='x', pady=5)

        # Time selector
        time_frame = tk.Frame(self)
        time_frame.pack(fill='x', pady=5)

        # Hours
        self.hour = ttk.Spinbox(time_frame, from_=0, to=23, width=4)
        self.hour.pack(side=tk.LEFT, padx=2)

        # Minutes
        self.minute = ttk.Spinbox(time_frame, from_=0, to=59, width=4)
        self.minute.pack(side=tk.LEFT, padx=2)

        # Set default time
        now = datetime.now()
        self.hour.set(now.hour)
        self.minute.set(now.minute)

    def get_timestamp(self):
        try:
            selected_date = self.calendar.get_date()
            dt = datetime.strptime(selected_date, '%Y-%m-%d')
            dt = dt.replace(hour=int(self.hour.get()), minute=int(self.minute.get()))
            return int(dt.timestamp())
        except ValueError:
            return None


class ClientApp:
    def __init__(self, master):
        self.master = master
        master.title("Attendance Manager")
        master.geometry("500x300")
        
        master.grid_rowconfigure(1, weight=1)
        master.grid_columnconfigure(0, weight=1)

        self.seance_ids = []
        self.student_ids = []

        # Initialize SSL socket
        self.socket = SSLSocket()
        if not self.socket.connect('127.0.0.1', 8081):
            master.destroy()
            return

        self.create_home_page()
        
        # Setup cleanup on window close
        master.protocol("WM_DELETE_WINDOW", self.on_closing)

    def on_closing(self):
        try:
            # Send disconnect request before closing
            self.send_request("<a>disconnect")
            self.socket.close()
        except:
            pass
        self.master.destroy()

    def create_home_page(self):
        self.clear_window()

        header = tk.Frame(self.master)
        header.grid(row=0, column=0, sticky="ew", padx=10, pady=(10,5))
        
        tk.Label(header, text="Liste des sessions", font=('Arial', 14, 'bold')).pack(side=tk.LEFT)
        tk.Button(header, text="Créer une session", command=self.create_seance).pack(side=tk.RIGHT)
        tk.Button(header, text="Gérer les etudiants", command=self.open_manage_students_window).pack(side=tk.RIGHT, padx=5)

        self.table = ttk.Treeview(self.master, show='headings')
        self.table.grid(row=1, column=0, sticky="nsew", padx=10, pady=5)

        self.load_table()
        self.table.bind('<Double-1>', self.on_table_select)

    def create_seance_window(self):
        dialog = tk.Toplevel(self.master)
        dialog.title("Création d'une séance")
        dialog.geometry("300x440")
        dialog.transient(self.master)
        dialog.grab_set()

        tk.Label(dialog, text="Nom de la séance:", font=('Arial', 11)).pack(pady=(20, 5))
        name_entry = tk.Entry(dialog, font=('Arial', 11))
        name_entry.pack(padx=20, fill='x')

        tk.Label(dialog, text="Date et heure:", font=('Arial', 11)).pack(pady=(20, 5))
        time_selector = TimeSelector(dialog)
        time_selector.pack(padx=20, pady=10)

        def submit():
            seance_name = name_entry.get()
            if not seance_name:
                messagebox.showerror("Erreur", "Merci d'entrer un nom de session")
                return

            unix_time = time_selector.get_timestamp()
            if unix_time is None:
                messagebox.showerror("Erreur", "Date et heure invalide")
                return

            request = f"<p>seance/{seance_name}/{unix_time}"
            self.send_request(request)
            dialog.destroy()
            self.load_table()

        tk.Button(dialog, text="Confirmer", command=submit, font=('Arial', 11)).pack(pady=20)


    def load_table(self):
        seances_response = self.send_request("<g>seance")
        seances = []
        if seances_response:
            for seance in seances_response.split(";"):
                if len(seance) > 1:
                    seance_info = seance.split(",")
                    seance_id = seance_info[0].split(":")[1]
                    seance_name = seance_info[1].split(":")[1]
                    unix_time = int(seance_info[2].split(":")[1])
                    seances.append((seance_id, seance_name, unix_time))

        self.table['columns'] = ['session', 'date', 'actions']
        self.table.heading('session', text='Nom de la session')
        self.table.heading('date', text='Date')
        self.table.heading('actions', text='Action')

        self.table.column('session', width=200, stretch=tk.NO)
        self.table.column('date', width=200, stretch=tk.NO)
        self.table.column('actions', width=80, anchor='center', stretch=tk.NO)

        for item in self.table.get_children():
            self.table.delete(item)

        for seance_id, seance_name, unix_time in seances:
            date_str = datetime.fromtimestamp(unix_time).strftime('%Y-%m-%d %H:%M')
            self.table.insert(
                '', 
                'end', 
                values=(seance_name, date_str, '🗑️'),
                tags=(seance_id,)
            )


        self.table.bind('<ButtonRelease-1>', self.on_home_table_click)


    def delete_seance(self, seance_id):
        request = f"<d>seance/{seance_id}"
        self.send_request(request)
        self.load_table()

    def on_home_table_click(self, event):
        region = self.table.identify("region", event.x, event.y)
        column = self.table.identify_column(event.x)
        row_id = self.table.identify_row(event.y)

        if region == "cell" and column == '#3':
            seance_id = self.table.item(row_id, 'tags')[0]
            self.delete_seance(seance_id)



    def create_seance(self):
        self.create_seance_window()

    def on_table_select(self, event):
        selection = self.table.selection()
        if selection:
            item = selection[0]
            seance_id = self.table.item(item)['tags'][0]
            seance_name = self.table.item(item)['values'][0]
            self.show_individual_seance_page(seance_id, seance_name)

    def show_individual_seance_page(self, seance_id, seance_name):
        self.clear_window()

        header = tk.Frame(self.master)
        header.grid(row=0, column=0, sticky="ew", padx=10, pady=(10, 5))
        
        tk.Button(header, text="← Retour", command=self.create_home_page).pack(side=tk.LEFT)
        tk.Label(header, text=seance_name, font=('Arial', 14, 'bold')).pack(side=tk.LEFT, padx=10)

        self.table = ttk.Treeview(self.master, show='headings')
        self.table.grid(row=1, column=0, sticky="nsew", padx=10, pady=5)

        self.load_students(seance_id)

        self.table.column('name', width=200)
        self.table.column('attendance', width=100)


    def load_students(self, seance_id):
        self.table['columns'] = ['name', 'attendance']
        self.table.heading('name', text='Nom')
        self.table.heading('attendance', text='Présence')

        for item in self.table.get_children():
            self.table.delete(item)

        response = self.send_request("<g>student")
        if response:
            students = response.split(";")
            for student in students:
                if len(student) > 1:
                    student_info = student.split(",")
                    student_id = student_info[0].split(":")[1]
                    student_name = student_info[1].split(":")[1]
                    attendance = self.get_student_attendance(seance_id, student_id)

                    self.table.insert(
                        '', 
                        'end', 
                        values=(student_name, '✓ Présent' if attendance == '1' else '○ Absent'),
                        tags=(student_id,)
                    )

        self.table.column('name', width=200, anchor='w') 
        self.table.column('attendance', width=100, anchor='center')

        self.table.bind('<Double-1>', lambda e: self.toggle_attendance(seance_id))


    def toggle_attendance(self, seance_id):
        selected_item = self.table.selection()
        if selected_item:
            student_id = self.table.item(selected_item[0], 'tags')[0]
            current_attendance = self.get_student_attendance(seance_id, student_id)
            new_attendance = '0' if current_attendance == '1' else '1'

            request = f"<p>attendance/{seance_id}/{student_id}/{new_attendance}"
            self.send_request(request)
            self.load_students(seance_id)



    def open_manage_students_window(self):
        self.clear_window()

        header = tk.Frame(self.master)
        header.grid(row=0, column=0, sticky="ew", padx=10, pady=(10, 5))
        
        tk.Button(header, text="← Retour", command=self.create_home_page).pack(side=tk.LEFT)
        tk.Label(header, text="Etudiants", font=('Arial', 14, 'bold')).pack(side=tk.LEFT, padx=10)
        tk.Button(header, text="Créer un étudiant", command=self.create_student).pack(side=tk.RIGHT)

        self.student_listbox = tk.Listbox(self.master, font=('Arial', 11))
        self.student_listbox.grid(row=1, column=0, sticky="nsew", padx=10, pady=5)
        self.load_students_list()

        tk.Button(self.master, text="Supprimer la selection", command=self.delete_student, fg='red').grid(row=2, column=0, pady=10)

    def load_students_list(self):
        self.student_listbox.delete(0, tk.END)
        self.student_ids = []
        response = self.send_request("<g>student")
        if response:
            students = response.split(";")
            for student in students:
                if student:
                    student_info = student.split(",")
                    student_id = student_info[0].split(":")[1]
                    student_name = student_info[1].split(":")[1]
                    self.student_ids.append(student_id)
                    self.student_listbox.insert(tk.END, student_name)

    def create_student(self):
        student_name = simpledialog.askstring("Création d'étudiant", "Entrer le nom complet: ")
        if student_name:
            request = f"<p>student/{student_name}"
            self.send_request(request)
            self.load_students_list()


    def delete_student(self):
        selected_index = self.student_listbox.curselection()
        if selected_index:
            student_id = self.student_ids[selected_index[0]]
            request = f"<d>student/{student_id}"
            self.send_request(request)
            self.load_students_list()

    def get_student_attendance(self, seance_id, student_id):
        request = f"<g>attendance/{seance_id}/{student_id}"
        response = self.send_request(request)
        if response:
            return response.split(",")[2].split(":")[1]
        return '0'
    
    def send_request(self, request):
        try:
            self.socket.send(request)
            response = self.socket.recv(1024).decode()
            
            # Handle special responses
            if response == "499/DISCONNECTED":
                messagebox.showinfo("Info", "Disconnected from server")
                self.master.destroy()
                return None
            elif response == "499/CLOSED":
                messagebox.showinfo("Info", "Server is shutting down")
                self.master.destroy()
                return None
            elif response.startswith("400"):
                messagebox.showerror("Error", "Bad Request")
                return None
            elif response.startswith("500"):
                messagebox.showerror("Error", "Server Error")
                return None
                
            return response.replace("202/", "")
        except ssl.SSLError as e:
            messagebox.showerror("SSL Error", f"SSL/TLS error: {str(e)}")
            return None
        except Exception as e:
            messagebox.showerror("Error", f"Connection error: {str(e)}")
            return None
    def clear_window(self):
        for widget in self.master.winfo_children():
            widget.destroy()
        self.master.grid_rowconfigure(1, weight=1)
        self.master.grid_columnconfigure(0, weight=1)


if __name__ == "__main__":
    root = tk.Tk()
    app = ClientApp(root)
    root.mainloop()