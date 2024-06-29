/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.features.command.commands.client

import jdk.jshell.execution.Util
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.command.CommandException
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.command.builder.CommandBuilder
import net.ccbluex.liquidbounce.features.command.builder.ParameterBuilder
import net.ccbluex.liquidbounce.features.module.QuickImports
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleCriticals
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleTeleport
import net.ccbluex.liquidbounce.utils.client.MovePacketType
import net.ccbluex.liquidbounce.utils.client.chat
import net.ccbluex.liquidbounce.utils.client.regular
import net.ccbluex.liquidbounce.utils.client.variable
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.floor

/**
 * Teleport Command
 *
 * Allows you to teleport.
 */
object CommandPlayerTeleport : QuickImports {

    private val decimalFormat = DecimalFormat("##0.000")

    fun createCommand(): Command {
        return CommandBuilder
            .begin("playerteleport")
            .alias("playertp", "ptp")
            .parameter(
                ParameterBuilder
                    .begin<String>("player")
                    .required()
                    .build(),
            )
            .parameter(
                ParameterBuilder
                    .begin<String>("copy")
                    .optional()
                    .build()
            )
            .handler { command, args ->
                val player = world.players.find { it.gameProfile.name.equals(args[0] as String, true) }
                    ?: throw CommandException(command.result("playerNotFound"))

                val y = if (ModuleTeleport.highTp) {
                        ModuleTeleport.highTpAmount
                    } else {
                        player.y
                    }

                if (args.size > 1 && args[1] == "copy") {
                    val clipboard = ".teleport ${player.x.toInt()} ${y.toInt()} ${player.z.toInt()}"

                    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(clipboard), null)

                    return@handler
                }

                ModuleTeleport.indicateTeleport(player.x, y.toDouble(), player.z)

                chat(
                    regular(
                        command.result(
                            "positionUpdated",
                            variable(decimalFormat.format(player.x)),
                            variable(decimalFormat.format(player.y)),
                            variable(decimalFormat.format(player.z))
                        )
                    )
                )
            }
            .build()
    }
}
