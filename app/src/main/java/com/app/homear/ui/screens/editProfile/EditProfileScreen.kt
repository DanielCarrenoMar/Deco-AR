package com.app.homear.ui.screens.editProfile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.app.homear.ui.theme.ColorVerde
import com.app.homear.ui.theme.CorporatePurple
import com.app.homear.ui.theme.HomeARTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import com.app.homear.ui.component.InputData
import java.io.File
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    viewModel: EditProfileViewModel? = null
) {
    val vm = viewModel ?: hiltViewModel<EditProfileViewModel>()

    val name by vm.name.collectAsState()
    val email by vm.email.collectAsState()
    val phone by vm.phone.collectAsState()
    val coverImageUri by vm.coverImageUri.collectAsState()
    val profileImageUri by vm.profileImageUri.collectAsState()

    val textField1 = remember { mutableStateOf("") }
    val textField2 = remember { mutableStateOf("") }
    val textField3 = remember { mutableStateOf("") }

    val context = LocalContext.current
    var portadaImageUri by remember { mutableStateOf<Uri?>(null) }
    var portadaSource by remember { mutableStateOf("") } // "camera" o "gallery"

    // Lanzador para galería de portada
    val galleryLauncherCover = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        portadaImageUri = uri
        portadaSource = if (uri != null) "gallery" else ""
    }

    // Lanzador para galería de perfil
    val galleryLauncherProfile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> vm.updateProfileImageUri(uri) }

    // Lanzador para cámara
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncherCover = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            portadaImageUri = cameraImageUri
            portadaSource = "camera"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF00664B))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 39.dp, bottom = 56.dp, start = 23.dp, end = 23.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/editar-perfil/back.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Atrás",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "Editar Perfil",
                color = Color(0xFFEEEEEE),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/editar-perfil/check.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Guardar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clickable {
                        vm.saveUserProfile()
                        onBack()
                    }
            )
        }
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/editar-perfil/card.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "Fondo tarjeta",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            // Aquí va el contenido del formulario encima del fondo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 31.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.size(144.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/editar-perfil/profile.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/editar-perfil/edit-profile.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Lapiz",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = 0.dp)
                                .clickable {
                                    galleryLauncherProfile.launch("image/*")
                                }
                        )
                    }
                }
                // Campos de formulario personalizados
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                    InputData(
                        dataValue = textField1,
                        label = "Nombre",
                        placeHolder = "Ingrese su nombre"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                    InputData(
                        dataValue = textField2,
                        label = "Correo electrónico",
                        placeHolder = "Ingrese su correo electrónico"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                    InputData(
                        dataValue = textField3,
                        label = "Teléfono",
                        placeHolder = "Ingrese su teléfono"
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                // Portada
                Text(
                    "Portada",
                    color = Color(0xFF646464),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 10.dp, start = 22.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFA4A4A4),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .weight(1f)
                            .heightIn(min = 100.dp)
                            .widthIn(min = 120.dp)
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                            .clickable {
                                // Tomar foto con cámara
                                val photoFile = File.createTempFile("cover_photo", ".jpg", context.cacheDir)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    context.packageName + ".provider",
                                    photoFile
                                )
                                cameraImageUri = uri
                                cameraLauncherCover.launch(uri)
                            }
                    ) {
                        if (portadaImageUri != null && portadaSource == "camera") {
                            Image(
                                painter = rememberAsyncImagePainter(portadaImageUri),
                                contentDescription = "Portada tomada",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("file:///android_asset/editar-perfil/camera.svg")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Tomar foto",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        Text(
                            "Tomar foto",
                            color = Color(0xFF7F7C7C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color(0xFFA4A4A4),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .weight(1f)
                            .heightIn(min = 100.dp)
                            .widthIn(min = 120.dp)
                            .padding(vertical = 12.dp, horizontal = 8.dp)
                            .clickable {
                                // Seleccionar de galería
                                galleryLauncherCover.launch("image/*")
                            }
                    ) {
                        if (portadaImageUri != null && portadaSource == "gallery") {
                            Image(
                                painter = rememberAsyncImagePainter(portadaImageUri),
                                contentDescription = "Portada galería",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("file:///android_asset/editar-perfil/image.svg")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Galería",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                        Text(
                            "Galería",
                            color = Color(0xFF7F7C7C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun EditProfileScreenPreview() {
    HomeARTheme {
        EditProfileScreen()
    }
}
