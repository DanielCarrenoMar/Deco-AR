package com.app.homear.ui.configuracion




import com.app.homear.R
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScrennConfiguracion()
{


    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
        ,
    )
    {
        //column para que los elementos se posicionen uno debajo de otro
        Column()
        {
            //contenedor para el titulo
            Box(modifier = Modifier.fillMaxWidth())
            {
                //titulo de la pantalla
                Text(
                    text = "Configuracion",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.W800,
                    color = Color.Magenta,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            //Opciones de la pantalla
            OptionConfiguracion("NOTIFICACIONES", R.drawable.icono_toggle_on)
            OptionConfiguracion("TEMA:CLARO", R.drawable.icono_light_mode)
            OptionConfiguracion("IDIOMA:ESPAÑOL", R.drawable.icono_sms)
            OptionConfiguracion("COMPARTIR APP", R.drawable.icono_share)
            OptionConfiguracion("AYUDA", R.drawable.icono_error)
            OptionConfiguracion("SOBRE NOSOTROS", R.drawable.icono_group)
        }
    }
}

/**
    esta funcion composable define a las celdas de las opciones
    @param nombre: define el texto que va a tener la opcion
    @param idImagen: recibe la direccion de donde esta almacenado el icono de la opcion,
                    dicha direccion la retorna el metodo R.drawable.nombreDelarchivo
 */
@Composable
fun OptionConfiguracion(nombre: String, idImagen: Int)
{
        Row(
            modifier = Modifier
                //el contenedor va a ocupar todo el ancho disponible
                .fillMaxWidth()
                // estilo del borde, el shape define la forma del borde
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp) // ancho del elemento


        )
        {
            // este es el contenedor de los elementos
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text(text = nombre,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                )
                // aqui se carga el icono
                Icon(
                    painter = painterResource(idImagen),
                    contentDescription = "icono Favorito",
                    modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
                    tint = Color.Magenta,

                )
            }
        }
        //este es el margin entre los elementos
        Spacer(modifier = Modifier.height(10.dp))
}


//composable para preview de la pantalla
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ScrennConfiguracion()
}
