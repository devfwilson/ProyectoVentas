drop database if exists Tienda1;
create database Tienda1;
use Tienda1;

create table Usuarios (
	idUsuario int auto_increment,
	correoUsuario varchar(128) not null,
	contraseñaUsuario varchar(128) not null,
    constraint pk_usuarios primary key (idUsuario)
);

create table Productos(
	idProducto int auto_increment,
	nombreProducto varchar(64) not null,
	precioProducto decimal(10,2) not null,
	stockProducto int not null,
    constraint pk_productos primary key (idProducto) 
);	

create table Compras(
	idCompra int auto_increment,
    estadoCompra enum('Pendiente','Completada','Cancelada') default 'Pendiente',
    estadoPago enum('Pendiente', 'Pagado')default'Pendiente',
    fechaCompra datetime default now(),
    constraint pk_compras primary key (idCompra)
);

create table DetalleCompras(
	idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    subtotal decimal(10,2) not null,
    constraint pk_detallecompras primary key (idCompra, idProducto),
    constraint fk_detalle_compras_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade,
    constraint fk_detalle_compras_productos foreign key (idProducto)
		references Productos(idProducto) on delete cascade
);

create table Facturas(
	idFactura int auto_increment,
    fecha datetime default now(),
    total decimal(10,2) not null,
	idCompra int not null,
	constraint pk_facturas primary key (idFactura),
	constraint fk_facturas_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade
);

-- ======================================
-- CRUD USUARIOS
-- ======================================
DELIMITER $$ 
create procedure sp_agregarUsuario(
	in p_correoUsuario varchar(128),
	in p_contraseñaUsuario varchar(128)
)
begin
	insert into Usuarios(correoUsuario, contraseñaUsuario)
	values(p_correoUsuario, p_contraseñaUsuario);
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_listarUsuarios()
begin
	select * from Usuarios;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_actualizarUsuario(
	in p_idUsuario int,
	in p_correoUsuario varchar(128),
	in p_contraseñaUsuario varchar(128)
)
begin
	update Usuarios
	set correoUsuario = p_correoUsuario,
		contraseñaUsuario = p_contraseñaUsuario
	where idUsuario = p_idUsuario;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_eliminarUsuario(in p_idUsuario int)
begin
	delete from Usuarios where idUsuario = p_idUsuario;
end;
$$
DELIMITER ;

-- ======================================
-- CRUD PRODUCTOS
-- ======================================
DELIMITER $$
create procedure sp_agregarProducto(
	in p_nombreProducto varchar(64),
	in p_precioProducto decimal(10,2),
	in p_stockProducto int
)
begin
	insert into Productos(nombreProducto, precioProducto, stockProducto)
	values(p_nombreProducto, p_precioProducto, p_stockProducto);
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_listarProductos()
begin
	select * from Productos;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_actualizarProducto(
	in p_idProducto int,
	in p_nombreProducto varchar(64),
	in p_precioProducto decimal(10,2),
	in p_stockProducto int
)
begin
	update Productos
	set nombreProducto = p_nombreProducto,
		precioProducto = p_precioProducto,
		stockProducto = p_stockProducto
	where idProducto = p_idProducto;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_eliminarProducto(in p_idProducto int)
begin
	delete from Productos where idProducto = p_idProducto;
end;
$$
DELIMITER ;

-- ======================================
-- CRUD COMPRAS
-- ======================================
DELIMITER $$
create procedure sp_agregarCompra(
    in p_estadoCompra enum('Pendiente','Completada','Cancelada'),
    in p_estadoPago enum('Pendiente','Pagado'),
    out p_idCompra int
)
begin
    insert into Compras(estadoCompra, estadoPago)
    values(p_estadoCompra, p_estadoPago);
    
    set p_idCompra = last_insert_id();
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_listarCompras()
begin
	select 
		idCompra,
		estadoCompra,
		estadoPago,
		fechaCompra
	from Compras;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_actualizarCompra(
	in p_idCompra int,
	in p_estadoCompra enum('Pendiente','Completada','Cancelada'),
	in p_estadoPago enum('Pendiente','Pagado')
)
begin
	update Compras
	set estadoCompra = p_estadoCompra,
		estadoPago = p_estadoPago
	where idCompra = p_idCompra;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_eliminarCompra(in p_idCompra int)
begin
	delete from Compras where idCompra = p_idCompra;
end;
$$
DELIMITER ;

-- ======================================
-- CRUD DETALLECOMPRAS
-- ======================================
DELIMITER $$
create procedure sp_agregarDetalleCompra(
	in p_idCompra int,
	in p_idProducto int,
	in p_cantidad int,
	in p_subtotal decimal(10,2)
)
begin
	insert into DetalleCompras(idCompra, idProducto, cantidad, subtotal)
	values(p_idCompra, p_idProducto, p_cantidad, p_subtotal);
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_listarDetalleCompras()
begin
	select * from DetalleCompras;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_actualizarDetalleCompra(
	in p_idCompra int,
	in p_idProducto int,
	in p_cantidad int,
	in p_subtotal decimal(10,2)
)
begin
	update DetalleCompras
	set cantidad = p_cantidad,
		subtotal = p_subtotal
	where idCompra = p_idCompra and idProducto = p_idProducto;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_eliminarDetalleCompra(
	in p_idCompra int,
	in p_idProducto int
)
begin
	delete from DetalleCompras where idCompra = p_idCompra and idProducto = p_idProducto;
end;
$$
DELIMITER ;

-- ======================================
-- CRUD FACTURAS
-- ======================================
DELIMITER $$
create procedure sp_agregarFactura(
	in p_total decimal(10,2),
	in p_idCompra int
)
begin
	insert into Facturas(total, idCompra)
	values(p_total, p_idCompra);
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_listarFacturas()
begin
	select * from Facturas;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_actualizarFactura(
	in p_idFactura int,
	in p_total decimal(10,2),
	in p_idCompra int
)
begin
	update Facturas
	set total = p_total,
		idCompra = p_idCompra
	where idFactura = p_idFactura;
end;
$$
DELIMITER ;

DELIMITER $$
create procedure sp_eliminarFactura(in p_idFactura int)
begin
	delete from Facturas where idFactura = p_idFactura;
end;
$$
DELIMITER ;



delimiter $$
create trigger tr_CalcularSubTotal_Before_Insert
before insert on DetalleCompras
for each row
begin
    declare precio decimal(10,2);
    
    select precioProducto into precio from Productos where idProducto = new.idProducto;
    
    set new.subtotal = precio * new.cantidad;
end$$
delimiter ;

delimiter $$
create trigger tr_CalcularTotal_Before_Insert
before insert on Facturas
for each row
begin
    declare total_compra decimal(10,2);
    
    select sum(subtotal) into total_compra 
    from detallecompras 
    where idCompra = new.idCompra;
    
    set new.total = total_compra;
end$$
delimiter ;
call sp_AgregarUsuario("b","b");
call sp_AgregarUsuario("a","a");
	

call sp_ListarUsuarios();

call sp_AgregarProducto('Tortrix de limón','2.00',30);
call sp_AgregarProducto('Galleta club extra','1.00',30);
call sp_AgregarProducto('Diccionario básico','30.00',30);
call sp_AgregarProducto('Botella pequeña de alcohol','3.00',30);
call sp_AgregarProducto('Botella de agua pura cielo','2.00',30);
call sp_AgregarProducto('botella de té fuze tea','3.5',30);
call sp_AgregarProducto('Botella de refresco rica roja','2.5',30);
call sp_AgregarProducto('Bolsa de sal yodada','1.00',30);
call sp_AgregarProducto('Chobix de barbacoa','1.00',30);
call sp_AgregarProducto('Tortrix picante','2.00',30);

call sp_ListarProductos();

call sp_listarCompras();
call sp_listarDetalleCompras();