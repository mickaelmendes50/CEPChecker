package co.mesquitalabs.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.mesquitalabs.api.getAddress
import co.mesquitalabs.model.Address

@Composable
fun Home(modifier: Modifier = Modifier) {
    val cep = remember { mutableStateOf("") }
    val error = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }
    val address = remember { mutableStateOf<Address?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
               modifier = modifier
                   .size(80.dp)
                   .clip(shape = RoundedCornerShape(50))
                   .background(MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = modifier.size(180.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = "Validador de CEP",
                style = MaterialTheme.typography.headlineMedium,
            )


            OutlinedTextField(
                value = cep.value,
                onValueChange = { newValue ->
                    val filteredText = newValue.filter {
                        !it.isWhitespace()
                        && it != '\n'
                    }
                    if (newValue.length <= 8)
                        cep.value = filteredText
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                label = { Text("CEP") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(50),
                isError = error.value
            )

            if (cep.value.length < 8)
            if (error.value)
                Text(
                    text = "CEP inválido"
                )

            Button(
                onClick = {
                    loading.value = true
                    getAddress(cep.value) { result ->
                        if (result == null) {
                            error.value = true
                            loading.value = false
                        } else {
                            error.value = false
                            address.value = result
                            loading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                enabled = cep.value.length == 8 && !loading.value,
                modifier = modifier.animateContentSize()
            ) {
                AnimatedContent(
                    targetState = loading.value,
                    label = "button_content_animation"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Buscar",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            address.value?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    modifier = modifier
                ) {
                    Column(
                        Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("CEP: ${it.cep}")
                        Text("Estado: ${it.state}")
                        Text("Cidade: ${it.city}")
                        Text("Bairro: ${it.neighborhood}")
                        Text("Rua: ${it.street}")
                        it.latitude?.let { lat ->
                            Text("Latitude: $lat")
                        }
                        it.longitude?.let { long ->
                            Text("Longitude: $long")
                        }
                    }
                }
            }
        }
    }
}
