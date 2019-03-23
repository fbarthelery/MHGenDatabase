package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.armor.list.ArmorListPagerActivity
import com.ghstudios.android.features.armorsetbuilder.armorselect.ArmorSelectActivity
import com.ghstudios.android.features.armorsetbuilder.talismans.TalismanMetadata
import com.ghstudios.android.features.armorsetbuilder.talismans.TalismanSelectActivity

/**
 * This is where the magic happens baby. Users can define a custom armor set in this fragment.
 */
class ASBFragment : Fragment(), ASBPieceContainerListener {
    // called by piece container when it requests a change to weapon slot count
    override fun onChangeWeaponSlots() {
        val dialog = ASBWeaponSlotsDialogFragment.newInstance(viewModel.session.numWeaponSlots)
        dialog.setTargetFragment(this, ASBDetailPagerActivity.REQUEST_CODE_SET_WEAPON_SLOTS)
        dialog.show(this.fragmentManager, "ASB_WEAPON_SLOTS")
    }

    // called by piece container when it requests a new talisman
    override fun onChangeTalisman() {
        val i = Intent(context, TalismanSelectActivity::class.java)
        startActivityForResult(i, ASBDetailPagerActivity.REQUEST_CODE_CREATE_TALISMAN)
    }

    // called by piece container when it requests an armor change
    override fun onChangeArmor(pieceIndex: Int) {
        val session = viewModel.session

        val i = Intent(context, ArmorSelectActivity::class.java)
        i.putExtra(ASBDetailPagerActivity.EXTRA_FROM_SET_BUILDER, true)
        i.putExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
        i.putExtra(ASBDetailPagerActivity.EXTRA_SET_RANK, session.rank)
        i.putExtra(ASBDetailPagerActivity.EXTRA_SET_HUNTER_TYPE, session.hunterType)

        startActivityForResult(i, ASBDetailPagerActivity.REQUEST_CODE_ADD_PIECE)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ASBDetailViewModel::class.java)
    }

    private lateinit var equipmentViews: List<ASBPieceContainer>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_asb, container, false)

        equipmentViews = listOf(
                view.findViewById(R.id.armor_builder_weapon),
                view.findViewById(R.id.armor_builder_head),
                view.findViewById(R.id.armor_builder_body),
                view.findViewById(R.id.armor_builder_arms),
                view.findViewById(R.id.armor_builder_waist),
                view.findViewById(R.id.armor_builder_legs),
                view.findViewById(R.id.armor_builder_talisman))

        for ((idx, equipView) in equipmentViews.withIndex()) {
            equipView.initialize(viewModel.session, idx, this, this)
        }

        viewModel.updatePieceEvent.observe(this, Observer {
            if (it != null) {
                equipmentViews.getOrNull(it)?.updateContents()
            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null)
            return

        if (resultCode == Activity.RESULT_OK) { // If the user canceled the request, we don't want to do anything.
            when (requestCode) {
                ASBDetailPagerActivity.REQUEST_CODE_SET_WEAPON_SLOTS -> {
                    val slots = data.getIntExtra(ASBWeaponSlotsDialogFragment.EXTRA_WEAPON_SLOTS, 3)
                    viewModel.setWeaponSlots(slots)
                }

                ASBDetailPagerActivity.REQUEST_CODE_ADD_PIECE -> {
                    val armorId = data.getLongExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, -1)
                    viewModel.addArmor(armorId)
                }

                ASBDetailPagerActivity.REQUEST_CODE_REMOVE_PIECE -> {
                    val pieceIndex = data.getIntExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, -1)
                    viewModel.removeArmorPiece(pieceIndex)
                }

                ASBDetailPagerActivity.REQUEST_CODE_ADD_DECORATION -> {
                    val decorationId = data.getLongExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, -1)
                    val pieceIndex = data.getIntExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, -1)
                    viewModel.bindDecoration(pieceIndex, decorationId)
                }

                ASBDetailPagerActivity.REQUEST_CODE_REMOVE_DECORATION -> {
                    val pieceIndex = data.getIntExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, -1)
                    val decorationIndex = data.getIntExtra(ASBDetailPagerActivity.EXTRA_DECORATION_INDEX, -1)
                    viewModel.unbindDecoration(pieceIndex, decorationIndex)
                }

                ASBDetailPagerActivity.REQUEST_CODE_CREATE_TALISMAN -> {
                    val metadata = data.getSerializableExtra(TalismanSelectActivity.EXTRA_TALISMAN) as? TalismanMetadata
                    if (metadata != null) {
                        viewModel.setTalisman(metadata)
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_asb, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            // The user wants to add an armor piece
            R.id.set_builder_add_piece -> {
                val intent = Intent(activity, ArmorListPagerActivity::class.java)
                intent.putExtra(ASBDetailPagerActivity.EXTRA_FROM_SET_BUILDER, true)

                startActivityForResult(intent, ASBDetailPagerActivity.REQUEST_CODE_ADD_PIECE)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** Called when the user clicks the drop-down arrow on an equipment view.  */
    fun onDecorationsMenuOpened() {
        equipmentViews.forEach { it.hideDecorations() }
    }
}