
<div align="center">

# IRLCraft (Em desenvolvimento)

**Traga seu corpo real para dentro do Minecraft.**

Captura de movimento via Kinect, transmitida em tempo real e renderizada como um avatar 3D dentro do jogo.

![Java](https://img.shields.io/badge/Java-1.8.9%20Forge-orange?logo=java)
![C#](https://img.shields.io/badge/C%23-.NET-purple?logo=csharp)
![Kinect](https://img.shields.io/badge/Sensor-Kinect%20360-blue)
![License](https://img.shields.io/badge/license-MIT-green)

</div>

---

## O que é

O **IRLCraft** conecta um sensor **Kinect 360** ao **Minecraft** (Forge 1.8.9). Ele captura o esqueleto do seu corpo em tempo real — cabeça, tronco, braços e pernas — e renderiza um avatar que imita seus movimentos dentro do mundo do jogo, ao lado do seu personagem.

É basicamente um **motion capture caseiro para Minecraft**: se você levanta o braço na vida real, o boneco levanta o braço no jogo.

<div align="center">
<img src="Demo2.png" width="420"/>
<img src="Demo5.png" width="420"/>
</div>

---

## Demonstração

<div align="center">
<img src="Demo4.png" width="600"/>

*O avatar reagindo em tempo real aos movimentos capturados pelo Kinect*
</div>

<table>
<tr>
<td><img src="Demo1.png"/></td>
<td><img src="Demo3.png"/></td>
</tr>
<tr>
<td><img src="Demo6.png"/></td>
<td><img src="Demo7.png"/></td>
</tr>
</table>

---

## Como funciona

O projeto é dividido em duas partes que se comunicam via rede:

```
      UDP         
   KinectCapture         ───────────────▶       KinectMod        
   (C# / .NET)             porta 25566        (Java / Forge 1.8.9) 
                                                                
  Lê o sensor Kinect,                       Recebe as posições e  
  extrai as 20 juntas                       desenha o avatar no   
  do esqueleto e                            mundo do jogo em      
  transmite via rede                        tempo real            

```

1. **`KinectCapture`** (C#) lê os dados do sensor Kinect, identifica a pessoa sendo rastreada e extrai a posição das 20 articulações do corpo a cada frame.
2. Essas posições são enviadas via **UDP** para o Minecraft, em tempo real.
3. **`KinectMod`** (Forge) recebe os dados numa thread própria e usa os hooks de renderização do Forge para desenhar um avatar 3D que espelha a pose capturada, ancorado ao lado do jogador.

---

## Estrutura do projeto

| Pasta | Descrição |
|---|---|
| `KinectCapture/` | Aplicação C# que lê o Kinect e transmite os dados |
| `KinectMod/` | Mod de Minecraft (Forge 1.8.9) que recebe e renderiza o avatar |

---

## Tecnologias

- **C# / .NET** — leitura do sensor via Kinect SDK
- **OpenTK** — visualização 3D do esqueleto no lado da captura
- **Java** — mod para Minecraft
- **Minecraft Forge 1.8.9** — hooks de renderização in-game
- **UDP** — comunicação em tempo real entre os dois lados

---

## Licença

Distribuído sob a licença MIT. Veja [`LICENSE`](LICENSE) para mais detalhes.

</div>
