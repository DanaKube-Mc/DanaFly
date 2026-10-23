# DanaFly 🪽

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Paper](https://img.shields.io/badge/Paper-1.21.1+-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-4.0.0-green.svg)]()

**DanaFly** est une refonte et modernisation complète de *TempFly* spécialement optimisée pour le serveur **DanaKube**. Il introduit un système de vol temporaire hautement configurable, fluide et riche en fonctionnalités, où le temps de vol peut être géré comme une véritable devise.

---

## ✨ Fonctionnalités Principales

* ⚡ **Paper 1.21.x & Java 21** : Performance native, architecture asynchrone et support complet des versions modernes.
* 🎨 **Moteur Textuel Adventure & MiniMessage** : Support natif des gradients (`<gradient:...>`), couleurs RGB hexadécimales (`&#RRGGBB` / `&x...`) et codes legacy ampersand (`&a`, `&c`).
* 🔄 **Rétrocompatibilité Totale** :
  * **Auto-migration transparente** : Importe et fusionne automatiquement les données et configs de `plugins/TempFly/` vers `plugins/DanaFly/`.
  * **Permissions avec fallback** : Support des nœuds `danafly.*` et repli automatique sur `tempfly.*` pour les configurations LuckPerms existantes.
  * **Base de données SQL** : Conservation de la table `TEMPFLY_DATA` pour préserver les bases existantes.
* 🔗 **Intégrations & Hooks Modernes** :
  * **WorldGuard 7** & **WorldEdit 7** (gestion native des régions sans réflexion).
  * **SuperiorSkyblock2 API (2024.1+)** : restrictions par île, rôles et missions.
  * **PlaceholderAPI** : expansions `%danafly_*%` et `%tempfly_*%`.
  * **Vault** : économie et permissions.
* 🧪 **Qualité & TDD** : Couverture par suite de tests unitaires et d'intégration via MockBukkit 1.21 et JUnit 5.

---

## 📌 Commandes & Alias

| Commande | Alias | Description | Permission |
|---|---|---|---|
| `/danafly` | `/df`, `/tempfly`, `/tf` | Commande principale d'administration et d'information | `danafly.time` |
| `/fly` | — | Activer ou désactiver son vol temporaire | `danafly.toggle.self` |
| `/danafly pay <joueur> <temps>` | `/tf pay ...` | Envoyer du temps de vol à un joueur | `danafly.pay` |
| `/danafly give <joueur> <temps>` | `/tf give ...` | Donner du temps (Admin) | `danafly.give` |
| `/danafly set <joueur> <temps>` | `/tf set ...` | Définir le temps d'un joueur (Admin) | `danafly.set` |
| `/danafly reload` | `/tf reload ...` | Recharger les configurations | `danafly.reload` |

*(Toutes les permissions `danafly.*` fonctionnent également avec `tempfly.*` pour assurer la rétrocompatibilité).*

---

## 🧩 Placeholders PAPI

Les placeholders suivants sont disponibles sous les préfixes `%danafly_*%` et `%tempfly_*%` :

* `%danafly_time-formatted%` : Temps restant formaté (ex: `1j 2h 30m 15s`).
* `%danafly_time-days%` : Jours restants.
* `%danafly_time-hours%` : Heures restantes.
* `%danafly_time-minutes%` : Minutes restantes.
* `%danafly_time-seconds%` : Secondes restantes.
* `%danafly_time-seconds-total%` : Total de secondes restantes.

---

## 💻 API Développeurs

Pour intégrer DanaFly à vos plugins internes :

```java
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.DanaFlyAPI;

DanaFlyAPI api = DanaFly.getAPI();

// Récupérer le temps de vol d'un joueur (en secondes)
double flightTime = api.getFlightTime(player.getUniqueId());

// Ajouter du temps de vol
api.addFlightTime(player.getUniqueId(), 3600.0);

// Définir le temps de vol
api.setFlightTime(player.getUniqueId(), 7200.0);
```

---

## 🔨 Compilation & Tests

```bash
# Compiler le plugin et exécuter tous les tests TDD
mvn clean verify

# Générer le fichier JAR final dans target/
mvn clean package
```
