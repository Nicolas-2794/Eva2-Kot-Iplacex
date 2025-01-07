package com.sombra.eva_kot_dos_v2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.sombra.eva_kot_dos_v2.room.AppDatabase
import com.sombra.eva_kot_dos_v2.room.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
        }
        setContent {
            ListadoComprasUI()
        }
    }
}

enum class Accion {
    LISTAR, CREAR, EDITAR
}

@Composable
fun ListadoComprasUI(){

    val contexto = LocalContext.current
    val (productos, setProductos) = remember { mutableStateOf(emptyList<Producto>()) }
    val (seleccion, setSeleccion) = remember{ mutableStateOf<Producto?>(null) }
    val (accion, setAccion) = remember{ mutableStateOf(Accion.LISTAR) }

    LaunchedEffect(productos) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance( contexto ).productoDao()
            setProductos( db.getAll() )
        }
    }

    val onSave = {
        setAccion(Accion.LISTAR)
        setProductos(emptyList())
    }


    when(accion) {
        Accion.CREAR    -> ProductoFormUI(null, onSave)
        Accion.EDITAR   -> ProductoFormUI(seleccion, onSave)
        else            -> ProductosListadoUI(
            productos,
            onAdd = { setAccion( Accion.CREAR ) },
            onEdit = { producto ->
                setSeleccion(producto)
                setAccion( Accion.EDITAR)
            }
        )
    }
}




@Composable
fun ProductosListadoUI(

    productos:List<Producto>, onAdd:() -> Unit = {},
    onEdit:(c:Producto) -> Unit = {}) {


    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAdd() },
                icon = {
                    Icon(
                        Icons.Filled.Create,
                        contentDescription = "agregar"
                    )
                },
                text = { Text("AGREGAR")},
                containerColor = Color(0xFF006400),
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
        },
        containerColor = Color(0xFFE0F7FA)
    ) { contentPadding ->
        if( productos.isNotEmpty() ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 60.dp)
            ) {
                items(productos) { producto ->
                    ProductoItemUI(producto) {
                        onEdit(producto)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay productos en la lista.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF006400)
                )
            }
        }
    }
}

@Composable
fun ProductoItemUI(producto: Producto, onSave:() -> Unit = {}){

    val alcanceCorr = rememberCoroutineScope()
    val contexto = LocalContext.current


    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        if (producto.comprado){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Producto Comprado",
                modifier = Modifier.clickable {
                    alcanceCorr.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        producto.comprado = false
                        dao.update(producto)
                        onSave()
                    }
                }
            )
        }else{
            Icon(
                Icons.Filled.Close,
                contentDescription = "Producto Sin Comprar",
                modifier = Modifier.clickable {
                    alcanceCorr.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        producto.comprado = true
                        dao.update(producto)
                        onSave()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = producto.nombre,
            modifier = Modifier.weight(2f),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF006400)
        )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar producto",
            modifier = Modifier.clickable {
                alcanceCorr.launch(Dispatchers.IO){
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    dao.delete(producto)
                    onSave()
                }
            }
        )
    }
}

@Composable
fun ProductoFormUI(c:Producto?, onSave:()->Unit = {}){
    val contexto = LocalContext.current
    val (nombre, setNombre) = remember { mutableStateOf(c?.nombre ?: "" ) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost( snackbarHostState) }
    ) {paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.account_box),
                contentDescription = "Imagen de carrito",
                Modifier.size(72.dp)
                )
            TextField(
                value = nombre,
                onValueChange = { setNombre(it) },
                label = {Text ("Ingrese el nombre del producto")}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance( contexto).productoDao()
                    val producto = Producto(0, nombre,false)
                    //if( producto.id > 0) {
                    if(c != null && c.id > 0){
                        dao.update(producto)
                    } else {
                        dao.insert(producto)
                    }
                    snackbarHostState.showSnackbar("Se ha guardado ${producto.nombre}")
                    onSave()
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400)
                )
                ) {
                Text(
                    text = "Guardar"
                    )
            }
            if(c?.id ?:0 > 0) {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao =
                            AppDatabase.getInstance(contexto).productoDao()
                        snackbarHostState.showSnackbar("Eliminando el producto ${c?.nombre}")
                        if( c != null) {
                            dao.delete( c )
                        }
                        onSave()
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400)
                    )
                    ) {
                    Text("Eliminar")
                }
            }
        }
    }
}