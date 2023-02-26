package jimenezli.nopeaceful.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class NoPeacefulMixin extends Screen {

	@Shadow private ButtonWidget difficultyButton;

	@Shadow private Difficulty difficulty;

	@Shadow protected abstract Text getDifficultyButtonText(Difficulty difficulty);

	@Shadow @Final private static Option[] OPTIONS;

	@Shadow private LockButtonWidget lockDifficultyButton;

	protected NoPeacefulMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	public void initInject(CallbackInfo ci) {
		int i = OPTIONS.length;
		if (this.difficultyButton != null) {
			this.difficultyButton.active = false;
			this.difficultyButton.setMessage(new LiteralText(""));
			if (this.client != null && this.client.world != null) {
				this.difficultyButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyButtonText(this.difficulty), (buttonWidget) -> {
					this.difficulty = Difficulty.byOrdinal(this.difficulty.getId() % 3 + 1);
					this.client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(this.difficulty));
					this.difficultyButton.setMessage(this.getDifficultyButtonText(this.difficulty));
				}));
				if (this.client.isIntegratedServerRunning() && !this.client.world.getLevelProperties().isHardcore()) {
					this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
					this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
				} else {
					this.difficultyButton.active = false;
				}
			}
		}
	}
}